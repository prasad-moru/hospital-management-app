package com.hospital.management.dao.impl;

import com.hospital.management.dao.DoctorDao;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.Page;
import com.hospital.management.model.PageRequest;
import com.hospital.management.util.DatabaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/** Oracle JDBC implementation of {@link DoctorDao}. */
public class DoctorDaoImpl implements DoctorDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDaoImpl.class);
    private static final Set<String> STATUSES = Set.of("ACTIVE", "INACTIVE");
    private static final int SEARCH_FIELDS = 12;
    private static final String SELECT_COLUMNS = """
            d.DOCTOR_ID, d.USER_ID, d.DEPARTMENT_ID, dep.DEPARTMENT_NAME,
            d.REGISTRATION_NUMBER, d.FIRST_NAME, d.LAST_NAME, d.SPECIALIZATION,
            d.QUALIFICATION, d.PHONE, d.CONSULTATION_FEE, d.STATUS AS DOCTOR_STATUS,
            u.USERNAME, u.EMAIL, u.STATUS AS USER_STATUS, d.CREATED_AT, d.UPDATED_AT
            """;
    private static final String JOIN = """
            FROM DOCTORS d
            JOIN USERS u ON u.USER_ID = d.USER_ID
            JOIN DEPARTMENTS dep ON dep.DEPARTMENT_ID = d.DEPARTMENT_ID
            JOIN ROLES r ON r.ROLE_ID = u.ROLE_ID
            """;
    private static final String FILTERS = """
            WHERE r.ROLE_NAME = 'DOCTOR'
              AND (? IS NULL OR
                   UPPER(d.REGISTRATION_NUMBER) LIKE ? ESCAPE '\\' OR
                   UPPER(d.FIRST_NAME) LIKE ? ESCAPE '\\' OR
                   UPPER(d.LAST_NAME) LIKE ? ESCAPE '\\' OR
                   UPPER(d.FIRST_NAME || ' ' || d.LAST_NAME) LIKE ? ESCAPE '\\' OR
                   UPPER(d.SPECIALIZATION) LIKE ? ESCAPE '\\' OR
                   UPPER(d.QUALIFICATION) LIKE ? ESCAPE '\\' OR
                   UPPER(d.PHONE) LIKE ? ESCAPE '\\' OR
                   UPPER(u.USERNAME) LIKE ? ESCAPE '\\' OR
                   UPPER(u.EMAIL) LIKE ? ESCAPE '\\' OR
                   UPPER(dep.DEPARTMENT_NAME) LIKE ? ESCAPE '\\' OR
                   UPPER(d.STATUS) LIKE ? ESCAPE '\\' OR
                   UPPER(u.STATUS) LIKE ? ESCAPE '\\')
              AND (? IS NULL OR d.DEPARTMENT_ID = ?)
              AND (? IS NULL OR d.STATUS = ?)
            """;

    @Override
    public Optional<Doctor> findById(Long doctorId) throws SQLException {
        if (doctorId == null) return Optional.empty();
        return findOne("SELECT " + SELECT_COLUMNS + JOIN
                + " WHERE r.ROLE_NAME = 'DOCTOR' AND d.DOCTOR_ID = ?", doctorId);
    }

    @Override
    public Optional<Doctor> findByRegistrationNumber(String registrationNumber) throws SQLException {
        if (registrationNumber == null || registrationNumber.isBlank()) return Optional.empty();
        return findOne("SELECT " + SELECT_COLUMNS + JOIN
                + " WHERE r.ROLE_NAME = 'DOCTOR' AND UPPER(d.REGISTRATION_NUMBER) = UPPER(?)",
                registrationNumber.trim());
    }

    private Optional<Doctor> findOne(String sql, Object value) throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapDoctor(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    public Page<Doctor> findAll(PageRequest request, Long departmentId, String status) throws SQLException {
        String normalizedStatus = normalizeStatusFilter(status);
        String sql = "SELECT " + SELECT_COLUMNS + JOIN + FILTERS
                + " ORDER BY d.FIRST_NAME, d.LAST_NAME, d.DOCTOR_ID"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Doctor> doctors = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = bindFilters(statement, request.getSearchTerm(), departmentId, normalizedStatus);
            statement.setInt(index++, request.getOffset());
            statement.setInt(index, request.getPageSize());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) doctors.add(mapDoctor(resultSet));
            }
        }
        return new Page<>(doctors, request.getPageNumber(), request.getPageSize(),
                count(request.getSearchTerm(), departmentId, normalizedStatus));
    }

    @Override
    public long count(String searchTerm, Long departmentId, String status) throws SQLException {
        String sql = "SELECT COUNT(1) " + JOIN + FILTERS;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindFilters(statement, searchTerm, departmentId, normalizeStatusFilter(status));
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        }
    }

    @Override
    public long createDoctorRecord(Connection connection, Doctor doctor) throws SQLException {
        String sql = """
                INSERT INTO DOCTORS
                    (USER_ID, DEPARTMENT_ID, REGISTRATION_NUMBER, FIRST_NAME, LAST_NAME,
                     SPECIALIZATION, QUALIFICATION, PHONE, CONSULTATION_FEE, STATUS)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"DOCTOR_ID"})) {
            statement.setLong(1, doctor.getUserId());
            bindDoctor(statement, doctor, 2);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("No generated doctor identifier returned");
    }

    @Override
    public boolean updateDoctorRecord(Connection connection, Doctor doctor) throws SQLException {
        String sql = """
                UPDATE DOCTORS SET DEPARTMENT_ID=?, REGISTRATION_NUMBER=?, FIRST_NAME=?, LAST_NAME=?,
                    SPECIALIZATION=?, QUALIFICATION=?, PHONE=?, CONSULTATION_FEE=?, STATUS=?,
                    UPDATED_AT=CURRENT_TIMESTAMP
                WHERE DOCTOR_ID=?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = bindDoctor(statement, doctor, 1);
            statement.setLong(index, doctor.getDoctorId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean updateDoctorStatus(Connection connection, Long doctorId, String status) throws SQLException {
        String normalized = normalizeRequiredStatus(status);
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE DOCTORS SET STATUS=?, UPDATED_AT=CURRENT_TIMESTAMP WHERE DOCTOR_ID=?")) {
            statement.setString(1, normalized);
            statement.setLong(2, doctorId);
            return statement.executeUpdate() == 1;
        }
    }

    @Override public boolean existsByRegistrationNumber(String value) throws SQLException {
        if (value == null || value.isBlank()) return false;
        return exists("SELECT 1 FROM DOCTORS WHERE UPPER(REGISTRATION_NUMBER)=UPPER(?) FETCH FIRST 1 ROW ONLY", value, null);
    }
    @Override public boolean existsByRegistrationNumberExcludingId(String value, Long id) throws SQLException {
        if (value == null || value.isBlank() || id == null) return false;
        return exists("SELECT 1 FROM DOCTORS WHERE UPPER(REGISTRATION_NUMBER)=UPPER(?) AND DOCTOR_ID<>? FETCH FIRST 1 ROW ONLY", value, id);
    }
    @Override public boolean hasFutureAppointments(Long id) throws SQLException {
        return existsById("SELECT 1 FROM APPOINTMENTS WHERE DOCTOR_ID=? AND TRUNC(APPOINTMENT_DATE)>=TRUNC(SYSDATE) AND STATUS IN ('SCHEDULED','CONFIRMED') FETCH FIRST 1 ROW ONLY", id);
    }
    @Override public boolean hasActiveSchedules(Long id) throws SQLException {
        return existsById("SELECT 1 FROM DOCTOR_SCHEDULES WHERE DOCTOR_ID=? AND IS_AVAILABLE=1 FETCH FIRST 1 ROW ONLY", id);
    }

    private boolean exists(String sql, String value, Long excludedId) throws SQLException {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value.trim());
            if (excludedId != null) statement.setLong(2, excludedId);
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next(); }
        }
    }
    private boolean existsById(String sql, Long id) throws SQLException {
        if (id == null) return false;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next(); }
        }
    }

    private int bindFilters(PreparedStatement statement, String search, Long departmentId, String status) throws SQLException {
        String term = search == null ? "" : search.trim();
        String pattern = likePattern(term);
        int index = 1;
        statement.setString(index++, term.isEmpty() ? null : pattern);
        for (int i = 0; i < SEARCH_FIELDS; i++) statement.setString(index++, pattern);
        if (departmentId == null || departmentId <= 0) statement.setNull(index++, java.sql.Types.NUMERIC); else statement.setLong(index++, departmentId);
        if (departmentId == null || departmentId <= 0) statement.setNull(index++, java.sql.Types.NUMERIC); else statement.setLong(index++, departmentId);
        statement.setString(index++, status);
        statement.setString(index++, status);
        return index;
    }

    private int bindDoctor(PreparedStatement statement, Doctor doctor, int index) throws SQLException {
        statement.setLong(index++, doctor.getDepartmentId());
        statement.setString(index++, doctor.getRegistrationNumber());
        statement.setString(index++, doctor.getFirstName());
        statement.setString(index++, doctor.getLastName());
        statement.setString(index++, doctor.getSpecialization());
        statement.setString(index++, doctor.getQualification());
        statement.setString(index++, doctor.getPhone());
        statement.setBigDecimal(index++, doctor.getConsultationFee());
        statement.setString(index++, doctor.getStatus());
        return index;
    }

    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(rs.getLong("DOCTOR_ID")); doctor.setUserId(rs.getLong("USER_ID"));
        doctor.setDepartmentId(rs.getLong("DEPARTMENT_ID")); doctor.setDepartmentName(rs.getString("DEPARTMENT_NAME"));
        doctor.setRegistrationNumber(rs.getString("REGISTRATION_NUMBER")); doctor.setFirstName(rs.getString("FIRST_NAME"));
        doctor.setLastName(rs.getString("LAST_NAME")); doctor.setSpecialization(rs.getString("SPECIALIZATION"));
        doctor.setQualification(rs.getString("QUALIFICATION")); doctor.setPhone(rs.getString("PHONE"));
        doctor.setConsultationFee(rs.getBigDecimal("CONSULTATION_FEE")); doctor.setStatus(rs.getString("DOCTOR_STATUS"));
        doctor.setUsername(rs.getString("USERNAME")); doctor.setEmail(rs.getString("EMAIL"));
        doctor.setUserStatus(rs.getString("USER_STATUS")); doctor.setCreatedAt(toDateTime(rs.getTimestamp("CREATED_AT")));
        doctor.setUpdatedAt(toDateTime(rs.getTimestamp("UPDATED_AT")));
        return doctor;
    }
    private java.time.LocalDateTime toDateTime(Timestamp value) { return value == null ? null : value.toLocalDateTime(); }
    private String normalizeStatusFilter(String status) { if (status == null || status.isBlank()) return null; return normalizeRequiredStatus(status); }
    private String normalizeRequiredStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!STATUSES.contains(normalized)) throw new IllegalArgumentException("Doctor status must be ACTIVE or INACTIVE");
        return normalized;
    }
    private String likePattern(String value) {
        return "%" + value.toUpperCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }
}
