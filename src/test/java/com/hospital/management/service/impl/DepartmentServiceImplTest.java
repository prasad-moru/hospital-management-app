package com.hospital.management.service.impl;
import com.hospital.management.dao.*;import com.hospital.management.model.*;import com.hospital.management.service.*;import com.hospital.management.validation.DepartmentValidator;import org.junit.jupiter.api.*;import java.sql.SQLException;import java.util.*;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
class DepartmentServiceImplTest{
 DepartmentDao dao;AuditLogDao audit;DepartmentServiceImpl service;AuditContext ctx=new AuditContext(1L,"127.0.0.1","test");
 @BeforeEach void setup(){dao=mock(DepartmentDao.class);audit=mock(AuditLogDao.class);service=new DepartmentServiceImpl(dao,new DepartmentValidator(),audit);}Department d(){return new Department(2L,"Cardiology","Heart","Block A","ACTIVE");}
 @Test void list()throws Exception{Page<Department>p=new Page<>(List.of(d()),1,10,1);when(dao.findAll(any())).thenReturn(p);assertEquals(1,service.listDepartments(new PageRequest(1,10,"")).getTotalElements());}
 @Test void createSuccess()throws Exception{when(dao.create(any())).thenReturn(4L);assertTrue(service.createDepartment(d(),ctx).isSuccess());verify(audit).recordEvent(eq("DEPARTMENT_CREATED"),any(),any(),eq(4L),any(),any());}
 @Test void duplicate()throws Exception{when(dao.existsByName(any())).thenReturn(true);assertEquals(ServiceResult.Status.DUPLICATE,service.createDepartment(d(),ctx).getStatus());}
 @Test void validation(){Department invalid=d();invalid.setDepartmentName(" ");assertEquals(ServiceResult.Status.VALIDATION_ERROR,service.createDepartment(invalid,ctx).getStatus());}
 @Test void updateSuccess()throws Exception{when(dao.findById(2L)).thenReturn(Optional.of(d()));assertTrue(service.updateDepartment(d(),ctx).isSuccess());}
 @Test void updateMissing()throws Exception{when(dao.findById(2L)).thenReturn(Optional.empty());assertEquals(ServiceResult.Status.NOT_FOUND,service.updateDepartment(d(),ctx).getStatus());}
 @Test void updateDuplicate()throws Exception{when(dao.findById(2L)).thenReturn(Optional.of(d()));when(dao.existsByNameExcludingId(any(),eq(2L))).thenReturn(true);assertEquals(ServiceResult.Status.DUPLICATE,service.updateDepartment(d(),ctx).getStatus());}
 @Test void activate()throws Exception{when(dao.findById(2L)).thenReturn(Optional.of(d()));assertTrue(service.activateDepartment(2L,ctx).isSuccess());}
 @Test void deactivate()throws Exception{when(dao.findById(2L)).thenReturn(Optional.of(d()));assertTrue(service.deactivateDepartment(2L,ctx).isSuccess());}
 @Test void deactivateConflict()throws Exception{when(dao.findById(2L)).thenReturn(Optional.of(d()));when(dao.hasActiveDoctors(2L)).thenReturn(true);assertEquals(ServiceResult.Status.CONFLICT,service.deactivateDepartment(2L,ctx).getStatus());}
 @Test void sqlFailureSafe()throws Exception{when(dao.existsByName(any())).thenThrow(new SQLException("internal"));assertEquals(ServiceResult.Status.SYSTEM_ERROR,service.createDepartment(d(),ctx).getStatus());}
 @Test void auditFailureDoesNotFail()throws Exception{when(dao.create(any())).thenReturn(3L);doThrow(new RuntimeException()).when(audit).recordEvent(any(),any(),any(),any(),any(),any());assertTrue(service.createDepartment(d(),ctx).isSuccess());}
}
