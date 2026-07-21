package com.hospital.management.service.impl;

import com.hospital.management.dao.AuditLogDao;
import com.hospital.management.dao.DepartmentDao;
import com.hospital.management.dao.DoctorDao;
import com.hospital.management.dao.RoleDao;
import com.hospital.management.dao.UserDao;
import com.hospital.management.dao.impl.AuditLogDaoImpl;
import com.hospital.management.dao.impl.DepartmentDaoImpl;
import com.hospital.management.dao.impl.DoctorDaoImpl;
import com.hospital.management.dao.impl.RoleDaoImpl;
import com.hospital.management.dao.impl.UserDaoImpl;
import com.hospital.management.model.AuditContext;
import com.hospital.management.model.Department;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.DoctorForm;
import com.hospital.management.model.Page;
import com.hospital.management.model.PageRequest;
import com.hospital.management.model.User;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.ServiceResult;
import com.hospital.management.util.DatabaseConnectionManager;
import com.hospital.management.util.PasswordUtil;
import com.hospital.management.util.SqlExceptionUtil;
import com.hospital.management.validation.DoctorValidator;
import com.hospital.management.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** JDBC transaction coordinator for Doctor Management. */
public class DoctorServiceImpl implements DoctorService {
    private static final Logger LOG = LoggerFactory.getLogger(DoctorServiceImpl.class);
    private static final Set<String> STATUSES = Set.of("ACTIVE", "INACTIVE");

    @FunctionalInterface
    public interface ConnectionProvider { Connection getConnection() throws SQLException; }

    private final DoctorDao doctors;
    private final UserDao users;
    private final DepartmentDao departments;
    private final RoleDao roles;
    private final DoctorValidator validator;
    private final AuditLogDao audit;
    private final ConnectionProvider connections;

    public DoctorServiceImpl() {
        this(new DoctorDaoImpl(), new UserDaoImpl(), new DepartmentDaoImpl(), new RoleDaoImpl(),
                new DoctorValidator(), new AuditLogDaoImpl(), DatabaseConnectionManager::getConnection);
    }

    public DoctorServiceImpl(DoctorDao doctors, UserDao users, DepartmentDao departments,
                             RoleDao roles, DoctorValidator validator, AuditLogDao audit) {
        this(doctors, users, departments, roles, validator, audit, DatabaseConnectionManager::getConnection);
    }

    public DoctorServiceImpl(DoctorDao doctors, UserDao users, DepartmentDao departments,
                             RoleDao roles, DoctorValidator validator, AuditLogDao audit,
                             ConnectionProvider connections) {
        this.doctors = doctors;
        this.users = users;
        this.departments = departments;
        this.roles = roles;
        this.validator = validator;
        this.audit = audit;
        this.connections = connections;
    }

    @Override
    public Page<Doctor> listDoctors(PageRequest request, Long departmentId, String status) {
        PageRequest normalized = request == null ? new PageRequest(1, 10, "")
                : new PageRequest(request.getPageNumber(), request.getPageSize(), request.getSearchTerm());
        Long department = departmentId != null && departmentId > 0 ? departmentId : null;
        String state = normalizeFilterStatus(status);
        try {
            return doctors.findAll(normalized, department, state);
        } catch (SQLException | IllegalArgumentException ex) {
            LOG.error("Doctor listing failed", ex);
            return new Page<>(List.of(), normalized.getPageNumber(), normalized.getPageSize(), 0);
        }
    }

    @Override
    public Optional<Doctor> getDoctor(Long doctorId) {
        if (doctorId == null || doctorId <= 0) return Optional.empty();
        try {
            return doctors.findById(doctorId);
        } catch (SQLException ex) {
            LOG.error("Doctor lookup failed for id {}", doctorId, ex);
            return Optional.empty();
        }
    }

    @Override
    public List<Department> getAvailableDepartments(Long currentDepartmentId) {
        try {
            Map<Long, Department> available = new LinkedHashMap<>();
            for (Department department : departments.findActiveDepartments()) {
                available.put(department.getDepartmentId(), department);
            }
            if (currentDepartmentId != null && currentDepartmentId > 0 && !available.containsKey(currentDepartmentId)) {
                departments.findById(currentDepartmentId).ifPresent(d -> available.put(d.getDepartmentId(), d));
            }
            List<Department> result = new ArrayList<>(available.values());
            result.sort(Comparator.comparing(Department::getDepartmentName, String.CASE_INSENSITIVE_ORDER));
            return result;
        } catch (SQLException ex) {
            LOG.error("Available department lookup failed", ex);
            return List.of();
        }
    }

    @Override
    public ServiceResult<Long> createDoctor(DoctorForm form, AuditContext context) {
        if (form == null) return validation(Map.of("form", "Doctor details are required"));
        normalize(form);
        ValidationResult checked = validator.validateCreate(form);
        if (checked.hasErrors()) return validation(checked.getErrors());
        try {
            Optional<Department> department = departments.findById(form.getDepartmentId());
            if (department.isEmpty()) return failure(ServiceResult.Status.NOT_FOUND, "Department not found");
            if (!"ACTIVE".equals(department.get().getStatus())) return conflict("Select an active department");
            if (doctors.existsByRegistrationNumber(form.getRegistrationNumber())) return duplicate("registrationNumber", "Registration number already exists");
            if (users.usernameExists(form.getUsername())) return duplicate("username", "Username already exists");
            if (users.emailExists(form.getEmail())) return duplicate("email", "Email already exists");
            Optional<Long> roleId = roles.findRoleIdByName("DOCTOR");
            if (roleId.isEmpty()) {
                LOG.error("Required DOCTOR role is missing");
                return systemError();
            }
            User user = toUser(form, roleId.get(), PasswordUtil.hashPassword(form.getPassword()));
            Doctor doctor = toDoctor(form);
            long doctorId = inTransaction(connection -> {
                long userId = users.create(connection, user);
                doctor.setUserId(userId);
                return doctors.createDoctorRecord(connection, doctor);
            });
            audit("DOCTOR_CREATED", doctorId, context);
            return ServiceResult.success("Doctor created", doctorId);
        } catch (SQLException ex) {
            LOG.error("Doctor creation failed", ex);
            return SqlExceptionUtil.isUniqueConstraintViolation(ex)
                    ? duplicate("registrationNumber", "Username, email or registration number already exists") : systemError();
        } catch (RuntimeException ex) {
            LOG.error("Doctor creation failed", ex);
            return systemError();
        }
    }

    @Override
    public ServiceResult<Void> updateDoctor(DoctorForm form, AuditContext context) {
        if (form == null || form.getDoctorId() == null || form.getDoctorId() <= 0)
            return failure(ServiceResult.Status.NOT_FOUND, "Doctor not found");
        normalize(form);
        ValidationResult checked = validator.validateEdit(form);
        if (checked.hasErrors()) return validationVoid(checked.getErrors());
        try {
            Optional<Doctor> found = doctors.findById(form.getDoctorId());
            if (found.isEmpty()) return failure(ServiceResult.Status.NOT_FOUND, "Doctor not found");
            Doctor existing = found.get();
            Optional<Department> target = departments.findById(form.getDepartmentId());
            if (target.isEmpty()) return failure(ServiceResult.Status.NOT_FOUND, "Department not found");
            boolean reassigned = !form.getDepartmentId().equals(existing.getDepartmentId());
            if (reassigned && !"ACTIVE".equals(target.get().getStatus())) return conflict("Doctors can only be reassigned to an active department");
            if (doctors.existsByRegistrationNumberExcludingId(form.getRegistrationNumber(), form.getDoctorId())) return duplicateVoid("registrationNumber", "Registration number already exists");
            if (users.usernameExistsExcludingUser(form.getUsername(), existing.getUserId())) return duplicateVoid("username", "Username already exists");
            if (users.emailExistsExcludingUser(form.getEmail(), existing.getUserId())) return duplicateVoid("email", "Email already exists");
            Doctor changed = toDoctor(form);
            changed.setUserId(existing.getUserId());
            String linkedUserStatus = "LOCKED".equals(existing.getUserStatus()) ? "LOCKED" : form.getStatus();
            inTransaction(connection -> {
                requireUpdated(users.updateUserIdentity(connection, existing.getUserId(), form.getUsername(), form.getEmail(), linkedUserStatus));
                if (form.getPassword() != null && !form.getPassword().isBlank())
                    requireUpdated(users.updatePassword(connection, existing.getUserId(), PasswordUtil.hashPassword(form.getPassword())));
                requireUpdated(doctors.updateDoctorRecord(connection, changed));
                return null;
            });
            audit("DOCTOR_UPDATED", form.getDoctorId(), context);
            return ServiceResult.success("Doctor updated", null);
        } catch (SQLException ex) {
            LOG.error("Doctor update failed for id {}", form.getDoctorId(), ex);
            return SqlExceptionUtil.isUniqueConstraintViolation(ex)
                    ? duplicateVoid("registrationNumber", "Username, email or registration number already exists") : systemErrorVoid();
        } catch (RuntimeException ex) {
            LOG.error("Doctor update failed for id {}", form.getDoctorId(), ex);
            return systemErrorVoid();
        }
    }

    @Override
    public ServiceResult<Void> activateDoctor(Long doctorId, AuditContext context) {
        if (doctorId == null || doctorId <= 0) return failure(ServiceResult.Status.NOT_FOUND, "Doctor not found");
        try {
            Optional<Doctor> found = doctors.findById(doctorId);
            if (found.isEmpty()) return failure(ServiceResult.Status.NOT_FOUND, "Doctor not found");
            Doctor doctor = found.get();
            Optional<Department> department = departments.findById(doctor.getDepartmentId());
            if (department.isEmpty() || !"ACTIVE".equals(department.get().getStatus())) return conflict("Doctor cannot be activated while the assigned department is inactive");
            if ("LOCKED".equals(doctor.getUserStatus())) return conflict("Locked user accounts must be unlocked separately before activation");
            inTransaction(connection -> {
                requireUpdated(doctors.updateDoctorStatus(connection, doctorId, "ACTIVE"));
                requireUpdated(users.updateStatus(connection, doctor.getUserId(), "ACTIVE"));
                return null;
            });
            audit("DOCTOR_ACTIVATED", doctorId, context);
            return ServiceResult.success("Doctor activated", null);
        } catch (SQLException ex) {
            LOG.error("Doctor activation failed for id {}", doctorId, ex);
            return systemErrorVoid();
        }
    }

    @Override
    public ServiceResult<Void> deactivateDoctor(Long doctorId, AuditContext context) {
        if (doctorId == null || doctorId <= 0) return failure(ServiceResult.Status.NOT_FOUND, "Doctor not found");
        try {
            Optional<Doctor> found = doctors.findById(doctorId);
            if (found.isEmpty()) return failure(ServiceResult.Status.NOT_FOUND, "Doctor not found");
            if (doctors.hasFutureAppointments(doctorId)) {
                audit("DOCTOR_DEACTIVATION_BLOCKED", doctorId, context);
                return conflict("Doctor has scheduled or confirmed future appointments");
            }
            if (doctors.hasActiveSchedules(doctorId)) {
                audit("DOCTOR_DEACTIVATION_BLOCKED", doctorId, context);
                return conflict("Doctor has active schedules");
            }
            Doctor doctor = found.get();
            inTransaction(connection -> {
                requireUpdated(doctors.updateDoctorStatus(connection, doctorId, "INACTIVE"));
                requireUpdated(users.updateStatus(connection, doctor.getUserId(), "INACTIVE"));
                return null;
            });
            audit("DOCTOR_DEACTIVATED", doctorId, context);
            return ServiceResult.success("Doctor deactivated", null);
        } catch (SQLException ex) {
            LOG.error("Doctor deactivation failed for id {}", doctorId, ex);
            return systemErrorVoid();
        }
    }

    private <T> T inTransaction(TransactionWork<T> work) throws SQLException {
        try (Connection connection = connections.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException ex) {
                try { connection.rollback(); } catch (SQLException rollbackFailure) { ex.addSuppressed(rollbackFailure); }
                throw ex;
            } finally {
                try { connection.setAutoCommit(originalAutoCommit); }
                catch (SQLException ex) { LOG.warn("Could not restore connection auto-commit state", ex); }
            }
        }
    }

    @FunctionalInterface private interface TransactionWork<T> { T execute(Connection connection) throws SQLException; }
    private void requireUpdated(boolean updated) throws SQLException { if (!updated) throw new SQLException("Expected record was not updated"); }

    private User toUser(DoctorForm form, Long roleId, String hash) {
        User user = new User();
        user.setUsername(form.getUsername()); user.setEmail(form.getEmail()); user.setPasswordHash(hash);
        user.setRoleId(roleId); user.setStatus(form.getStatus());
        return user;
    }

    private Doctor toDoctor(DoctorForm form) {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(form.getDoctorId()); doctor.setDepartmentId(form.getDepartmentId());
        doctor.setRegistrationNumber(form.getRegistrationNumber()); doctor.setFirstName(form.getFirstName());
        doctor.setLastName(form.getLastName()); doctor.setSpecialization(form.getSpecialization());
        doctor.setQualification(form.getQualification()); doctor.setPhone(form.getPhone());
        doctor.setConsultationFee(new BigDecimal(form.getConsultationFee())); doctor.setStatus(form.getStatus());
        return doctor;
    }

    private void normalize(DoctorForm form) {
        form.setUsername(lower(form.getUsername())); form.setEmail(lower(form.getEmail()));
        form.setRegistrationNumber(upper(form.getRegistrationNumber())); form.setFirstName(trim(form.getFirstName()));
        form.setLastName(trim(form.getLastName())); form.setSpecialization(trim(form.getSpecialization()));
        form.setQualification(trim(form.getQualification())); form.setPhone(trim(form.getPhone()));
        form.setConsultationFee(trim(form.getConsultationFee())); form.setStatus(upper(form.getStatus()));
    }

    private String normalizeFilterStatus(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = upper(value);
        return STATUSES.contains(normalized) ? normalized : null;
    }
    private String trim(String value) { return value == null ? null : value.trim(); }
    private String lower(String value) { String v = trim(value); return v == null ? null : v.toLowerCase(Locale.ROOT); }
    private String upper(String value) { String v = trim(value); return v == null ? null : v.toUpperCase(Locale.ROOT); }

    private void audit(String action, Long doctorId, AuditContext context) {
        try {
            AuditContext safe = context == null ? new AuditContext(null, null, null) : context;
            audit.recordEvent(action, safe.userId(), "DOCTOR", doctorId, safe.ipAddress(), safe.userAgent());
        } catch (RuntimeException ex) {
            LOG.warn("Doctor audit event failed: {}", action, ex);
        }
    }

    private ServiceResult<Long> validation(Map<String, String> errors) { return ServiceResult.failure(ServiceResult.Status.VALIDATION_ERROR, "Please correct the highlighted fields", errors); }
    private ServiceResult<Void> validationVoid(Map<String, String> errors) { return ServiceResult.failure(ServiceResult.Status.VALIDATION_ERROR, "Please correct the highlighted fields", errors); }
    private ServiceResult<Long> duplicate(String field, String message) { return ServiceResult.failure(ServiceResult.Status.DUPLICATE, message, Map.of(field, message)); }
    private ServiceResult<Void> duplicateVoid(String field, String message) { return ServiceResult.failure(ServiceResult.Status.DUPLICATE, message, Map.of(field, message)); }
    private <T> ServiceResult<T> conflict(String message) { return failure(ServiceResult.Status.CONFLICT, message); }
    private <T> ServiceResult<T> failure(ServiceResult.Status status, String message) { return ServiceResult.failure(status, message, Map.of()); }
    private ServiceResult<Long> systemError() { return failure(ServiceResult.Status.SYSTEM_ERROR, "Doctor service is temporarily unavailable"); }
    private ServiceResult<Void> systemErrorVoid() { return failure(ServiceResult.Status.SYSTEM_ERROR, "Doctor service is temporarily unavailable"); }
}
