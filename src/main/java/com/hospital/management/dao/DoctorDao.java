package com.hospital.management.dao;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.Page;
import com.hospital.management.model.PageRequest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/** Persistence operations for doctor profiles and their joined reference data. */
public interface DoctorDao {
    Optional<Doctor> findById(Long doctorId) throws SQLException;
    Optional<Doctor> findByRegistrationNumber(String registrationNumber) throws SQLException;
    Page<Doctor> findAll(PageRequest pageRequest, Long departmentId, String status) throws SQLException;
    long count(String searchTerm, Long departmentId, String status) throws SQLException;
    long createDoctorRecord(Connection connection, Doctor doctor) throws SQLException;
    boolean updateDoctorRecord(Connection connection, Doctor doctor) throws SQLException;
    boolean updateDoctorStatus(Connection connection, Long doctorId, String status) throws SQLException;
    boolean existsByRegistrationNumber(String registrationNumber) throws SQLException;
    boolean existsByRegistrationNumberExcludingId(String registrationNumber, Long doctorId) throws SQLException;
    boolean hasFutureAppointments(Long doctorId) throws SQLException;
    boolean hasActiveSchedules(Long doctorId) throws SQLException;
}
