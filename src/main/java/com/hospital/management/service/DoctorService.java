package com.hospital.management.service;

import com.hospital.management.model.AuditContext;
import com.hospital.management.model.Department;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.DoctorForm;
import com.hospital.management.model.Page;
import com.hospital.management.model.PageRequest;

import java.util.List;
import java.util.Optional;

/** Business operations for ADMIN Doctor Management. */
public interface DoctorService {
    /** Returns a filtered page of doctors. */
    Page<Doctor> listDoctors(PageRequest pageRequest, Long departmentId, String status);

    /** Finds a doctor without exposing password data. */
    Optional<Doctor> getDoctor(Long doctorId);

    /** Returns assignable active departments and, when needed, the current inactive department. */
    List<Department> getAvailableDepartments(Long currentDepartmentId);

    /** Creates linked user and doctor records atomically. */
    ServiceResult<Long> createDoctor(DoctorForm form, AuditContext auditContext);

    /** Updates linked user and doctor records atomically. */
    ServiceResult<Void> updateDoctor(DoctorForm form, AuditContext auditContext);

    /** Activates the linked doctor and user records atomically. */
    ServiceResult<Void> activateDoctor(Long doctorId, AuditContext auditContext);

    /** Deactivates the linked records when no schedules or future appointments block it. */
    ServiceResult<Void> deactivateDoctor(Long doctorId, AuditContext auditContext);
}
