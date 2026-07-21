package com.hospital.management.service;import com.hospital.management.model.*;import java.util.Optional;
/** ADMIN and RECEPTIONIST patient-management operations. */
public interface PatientService{Page<Patient> listPatients(PageRequest r,String status);Optional<Patient> getPatient(Long id);ServiceResult<Long> createPatient(PatientForm f,AuditContext a);ServiceResult<Void> updatePatient(PatientForm f,AuditContext a);ServiceResult<Void> activatePatient(Long id,AuditContext a);ServiceResult<Void> deactivatePatient(Long id,AuditContext a);}
