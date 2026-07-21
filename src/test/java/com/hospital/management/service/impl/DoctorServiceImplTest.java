package com.hospital.management.service.impl;

import com.hospital.management.dao.*;
import com.hospital.management.model.*;
import com.hospital.management.service.ServiceResult;
import com.hospital.management.validation.DoctorValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {
    @Mock DoctorDao doctors; @Mock UserDao users; @Mock DepartmentDao departments;
    @Mock RoleDao roles; @Mock AuditLogDao audit; @Mock Connection connection;
    DoctorServiceImpl service;
    final AuditContext context = new AuditContext(7L,"127.0.0.1","JUnit");

    @BeforeEach void setUp() throws Exception {
        lenient().when(connection.getAutoCommit()).thenReturn(true);
        service = new DoctorServiceImpl(doctors,users,departments,roles,new DoctorValidator(),audit,()->connection);
    }

    @Test void listsAndGetsDoctorsSafely() throws Exception {
        Page<Doctor> expected=new Page<>(List.of(doctor()),1,10,1);when(doctors.findAll(any(),eq(2L),eq("ACTIVE"))).thenReturn(expected);
        assertSame(expected,service.listDoctors(new PageRequest(1,10,"demo"),2L,"active"));
        when(doctors.findById(5L)).thenReturn(Optional.of(doctor()));assertTrue(service.getDoctor(5L).isPresent());assertTrue(service.getDoctor(null).isEmpty());
    }

    @Test void listSqlFailureReturnsEmptyPage() throws Exception {
        when(doctors.findAll(any(),isNull(),isNull())).thenThrow(new SQLException("private detail"));
        assertTrue(service.listDoctors(null,null,"invalid").getContent().isEmpty());
    }

    @Test void availableDepartmentsIncludeCurrentInactiveAndSort() throws Exception {
        Department active=department(1L,"Cardiology","ACTIVE"),inactive=department(2L,"Archived","INACTIVE");
        when(departments.findActiveDepartments()).thenReturn(List.of(active));when(departments.findById(2L)).thenReturn(Optional.of(inactive));
        List<Department> result=service.getAvailableDepartments(2L);assertEquals(List.of(inactive,active),result);
    }

    @Test void createSuccessCommitsLinkedRecordsAndAudits() throws Exception {
        readyCreate();when(users.create(eq(connection),any(User.class))).thenReturn(20L);when(doctors.createDoctorRecord(eq(connection),any(Doctor.class))).thenReturn(30L);
        ServiceResult<Long> result=service.createDoctor(form(),context);
        assertEquals(ServiceResult.Status.SUCCESS,result.getStatus());assertEquals(30L,result.getData());verify(connection).setAutoCommit(false);verify(connection).commit();verify(connection,never()).rollback();
        verify(audit).recordEvent("DOCTOR_CREATED",7L,"DOCTOR",30L,"127.0.0.1","JUnit");
    }

    @Test void createValidationAndBusinessConflictsStopBeforeTransaction() throws Exception {
        DoctorForm invalid=form();invalid.setPassword("short");assertEquals(ServiceResult.Status.VALIDATION_ERROR,service.createDoctor(invalid,context).getStatus());
        DoctorForm valid=form();when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"Old","INACTIVE")));
        assertEquals(ServiceResult.Status.CONFLICT,service.createDoctor(valid,context).getStatus());verifyNoInteractions(connection);
    }

    @Test void createDuplicateChecksAreFieldSpecific() throws Exception {
        when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General Medicine","ACTIVE")));
        when(doctors.existsByRegistrationNumber(anyString())).thenReturn(true);assertTrue(service.createDoctor(form(),context).getErrors().containsKey("registrationNumber"));
        reset(doctors);when(users.usernameExists(anyString())).thenReturn(true);assertTrue(service.createDoctor(form(),context).getErrors().containsKey("username"));
        reset(users);when(users.emailExists(anyString())).thenReturn(true);assertTrue(service.createDoctor(form(),context).getErrors().containsKey("email"));
    }

    @Test void missingDoctorRoleReturnsSystemError() throws Exception {
        when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General Medicine","ACTIVE")));when(roles.findRoleIdByName("DOCTOR")).thenReturn(Optional.empty());
        assertEquals(ServiceResult.Status.SYSTEM_ERROR,service.createDoctor(form(),context).getStatus());
    }

    @Test void doctorInsertAndCommitFailuresRollback() throws Exception {
        readyCreate();when(users.create(eq(connection),any())).thenReturn(20L);when(doctors.createDoctorRecord(eq(connection),any())).thenThrow(new SQLException("insert"));
        assertEquals(ServiceResult.Status.SYSTEM_ERROR,service.createDoctor(form(),context).getStatus());verify(connection).rollback();
        reset(connection,doctors,users);when(connection.getAutoCommit()).thenReturn(true);readyCreate();when(users.create(eq(connection),any())).thenReturn(20L);when(doctors.createDoctorRecord(eq(connection),any())).thenReturn(30L);doThrow(new SQLException("commit")).when(connection).commit();
        assertEquals(ServiceResult.Status.SYSTEM_ERROR,service.createDoctor(form(),context).getStatus());verify(connection).rollback();
    }

    @Test void updateSucceedsWithoutOrWithPassword() throws Exception {
        readyUpdate();DoctorForm noPassword=form();noPassword.setDoctorId(5L);noPassword.setPassword("");noPassword.setConfirmPassword("");
        assertTrue(service.updateDoctor(noPassword,context).isSuccess());verify(users,never()).updatePassword(any(),anyLong(),anyString());verify(connection).commit();
        reset(connection);when(connection.getAutoCommit()).thenReturn(true);DoctorForm withPassword=form();withPassword.setDoctorId(5L);
        assertTrue(service.updateDoctor(withPassword,context).isSuccess());verify(users).updatePassword(eq(connection),eq(10L),argThat(hash->hash.startsWith("$2")));
    }

    @Test void updateMissingAndDuplicateUserReturnSafeStatuses() throws Exception {
        DoctorForm form=form();form.setDoctorId(5L);when(doctors.findById(5L)).thenReturn(Optional.empty());assertEquals(ServiceResult.Status.NOT_FOUND,service.updateDoctor(form,context).getStatus());
        when(doctors.findById(5L)).thenReturn(Optional.of(doctor()));when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General","ACTIVE")));when(users.usernameExistsExcludingUser(anyString(),eq(10L))).thenReturn(true);
        assertEquals(ServiceResult.Status.DUPLICATE,service.updateDoctor(form,context).getStatus());
    }

    @Test void activateSucceedsButInactiveDepartmentAndLockedUserBlock() throws Exception {
        Doctor doctor=doctor();when(doctors.findById(5L)).thenReturn(Optional.of(doctor));when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General","ACTIVE")));when(doctors.updateDoctorStatus(connection,5L,"ACTIVE")).thenReturn(true);when(users.updateStatus(connection,10L,"ACTIVE")).thenReturn(true);
        assertTrue(service.activateDoctor(5L,context).isSuccess());verify(connection).commit();
        when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General","INACTIVE")));assertEquals(ServiceResult.Status.CONFLICT,service.activateDoctor(5L,context).getStatus());
        when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General","ACTIVE")));doctor.setUserStatus("LOCKED");assertEquals(ServiceResult.Status.CONFLICT,service.activateDoctor(5L,context).getStatus());
    }

    @Test void deactivateSucceedsOrIsBlockedAndAudited() throws Exception {
        when(doctors.findById(5L)).thenReturn(Optional.of(doctor()));when(doctors.updateDoctorStatus(connection,5L,"INACTIVE")).thenReturn(true);when(users.updateStatus(connection,10L,"INACTIVE")).thenReturn(true);
        assertTrue(service.deactivateDoctor(5L,context).isSuccess());verify(connection).commit();verify(audit).recordEvent("DOCTOR_DEACTIVATED",7L,"DOCTOR",5L,"127.0.0.1","JUnit");
        when(doctors.hasFutureAppointments(5L)).thenReturn(true);assertEquals(ServiceResult.Status.CONFLICT,service.deactivateDoctor(5L,context).getStatus());verify(audit).recordEvent("DOCTOR_DEACTIVATION_BLOCKED",7L,"DOCTOR",5L,"127.0.0.1","JUnit");
        when(doctors.hasFutureAppointments(5L)).thenReturn(false);when(doctors.hasActiveSchedules(5L)).thenReturn(true);assertEquals(ServiceResult.Status.CONFLICT,service.deactivateDoctor(5L,context).getStatus());
    }

    @Test void auditFailureDoesNotFailCommittedOperation() throws Exception {
        readyCreate();when(users.create(eq(connection),any())).thenReturn(20L);when(doctors.createDoctorRecord(eq(connection),any())).thenReturn(30L);doThrow(new RuntimeException("audit unavailable")).when(audit).recordEvent(anyString(),any(),anyString(),any(),any(),any());
        assertTrue(service.createDoctor(form(),context).isSuccess());verify(connection).commit();
    }

    private void readyCreate() throws Exception {when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General Medicine","ACTIVE")));when(roles.findRoleIdByName("DOCTOR")).thenReturn(Optional.of(3L));}
    private void readyUpdate() throws Exception {when(doctors.findById(5L)).thenReturn(Optional.of(doctor()));when(departments.findById(2L)).thenReturn(Optional.of(department(2L,"General","ACTIVE")));when(users.updateUserIdentity(eq(connection),eq(10L),anyString(),anyString(),anyString())).thenReturn(true);when(users.updatePassword(eq(connection),eq(10L),anyString())).thenReturn(true);when(doctors.updateDoctorRecord(eq(connection),any())).thenReturn(true);}
    private DoctorForm form(){DoctorForm f=new DoctorForm();f.setUsername("Demo.User");f.setEmail("DEMO@EXAMPLE.COM");f.setPassword("Strong@123");f.setConfirmPassword("Strong@123");f.setDepartmentId(2L);f.setRegistrationNumber("reg-1");f.setFirstName("Demo");f.setLastName("Doctor");f.setSpecialization("General Medicine");f.setQualification("MBBS");f.setPhone("9000000001");f.setConsultationFee("500.00");f.setStatus("ACTIVE");return f;}
    private Doctor doctor(){Doctor d=new Doctor();d.setDoctorId(5L);d.setUserId(10L);d.setDepartmentId(2L);d.setFirstName("Demo");d.setLastName("Doctor");d.setUserStatus("INACTIVE");return d;}
    private Department department(Long id,String name,String status){return new Department(id,name,"","",status);}
}
