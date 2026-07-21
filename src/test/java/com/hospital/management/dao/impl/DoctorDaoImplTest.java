package com.hospital.management.dao.impl;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.PageRequest;
import com.hospital.management.util.DatabaseConnectionManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DoctorDaoImplTest {
    @Test void findByIdMapsCompleteJoinedRow() throws Exception {
        Connection c=mock(Connection.class);PreparedStatement p=mock(PreparedStatement.class);ResultSet r=mock(ResultSet.class);
        when(c.prepareStatement(anyString())).thenReturn(p);when(p.executeQuery()).thenReturn(r);when(r.next()).thenReturn(true);
        when(r.getLong("DOCTOR_ID")).thenReturn(7L);when(r.getLong("USER_ID")).thenReturn(8L);when(r.getLong("DEPARTMENT_ID")).thenReturn(9L);
        when(r.getString("DEPARTMENT_NAME")).thenReturn("Cardiology");when(r.getString("REGISTRATION_NUMBER")).thenReturn("REG-7");
        when(r.getString("FIRST_NAME")).thenReturn("Asha");when(r.getString("LAST_NAME")).thenReturn("Rao");when(r.getString("SPECIALIZATION")).thenReturn("Cardiology");
        when(r.getString("QUALIFICATION")).thenReturn("MD");when(r.getString("PHONE")).thenReturn("1234567890");when(r.getBigDecimal("CONSULTATION_FEE")).thenReturn(new BigDecimal("500.00"));
        when(r.getString("DOCTOR_STATUS")).thenReturn("ACTIVE");when(r.getString("USERNAME")).thenReturn("asha.rao");when(r.getString("EMAIL")).thenReturn("asha@example.invalid");when(r.getString("USER_STATUS")).thenReturn("ACTIVE");
        Timestamp timestamp=Timestamp.valueOf(LocalDateTime.of(2026,1,1,10,0));when(r.getTimestamp("CREATED_AT")).thenReturn(timestamp);when(r.getTimestamp("UPDATED_AT")).thenReturn(timestamp);
        try(MockedStatic<DatabaseConnectionManager> manager=mockStatic(DatabaseConnectionManager.class)){manager.when(DatabaseConnectionManager::getConnection).thenReturn(c);Doctor d=new DoctorDaoImpl().findById(7L).orElseThrow();assertEquals("Asha Rao",d.getFullName());assertEquals(new BigDecimal("500.00"),d.getConsultationFee());assertEquals("Cardiology",d.getDepartmentName());}
        verify(p).setObject(1,7L);
    }
    @Test void findByIdReturnsEmpty()throws Exception{Connection c=mock(Connection.class);PreparedStatement p=mock(PreparedStatement.class);ResultSet r=mock(ResultSet.class);when(c.prepareStatement(anyString())).thenReturn(p);when(p.executeQuery()).thenReturn(r);when(r.next()).thenReturn(false);try(MockedStatic<DatabaseConnectionManager> m=mockStatic(DatabaseConnectionManager.class)){m.when(DatabaseConnectionManager::getConnection).thenReturn(c);assertTrue(new DoctorDaoImpl().findById(1L).isEmpty());}}
    @Test void findByRegistrationBindsNormalizedValue()throws Exception{Connection c=mock(Connection.class);PreparedStatement p=mock(PreparedStatement.class);ResultSet r=mock(ResultSet.class);when(c.prepareStatement(anyString())).thenReturn(p);when(p.executeQuery()).thenReturn(r);try(MockedStatic<DatabaseConnectionManager> m=mockStatic(DatabaseConnectionManager.class)){m.when(DatabaseConnectionManager::getConnection).thenReturn(c);new DoctorDaoImpl().findByRegistrationNumber(" REG-1 ");}verify(p).setObject(1,"REG-1");}
    @Test void createReturnsGeneratedKeyWithoutOwningTransaction()throws Exception{Connection c=mock(Connection.class);PreparedStatement p=mock(PreparedStatement.class);ResultSet keys=mock(ResultSet.class);when(c.prepareStatement(anyString(),any(String[].class))).thenReturn(p);when(p.getGeneratedKeys()).thenReturn(keys);when(keys.next()).thenReturn(true);when(keys.getLong(1)).thenReturn(42L);assertEquals(42L,new DoctorDaoImpl().createDoctorRecord(c,doctor()));verify(c,never()).commit();verify(c,never()).rollback();verify(c,never()).close();}
    @Test void createFailsWithoutGeneratedKey()throws Exception{Connection c=mock(Connection.class);PreparedStatement p=mock(PreparedStatement.class);ResultSet keys=mock(ResultSet.class);when(c.prepareStatement(anyString(),any(String[].class))).thenReturn(p);when(p.getGeneratedKeys()).thenReturn(keys);assertThrows(SQLException.class,()->new DoctorDaoImpl().createDoctorRecord(c,doctor()));}
    @Test void updateReflectsAffectedRows()throws Exception{Connection c=mock(Connection.class);PreparedStatement p=mock(PreparedStatement.class);when(c.prepareStatement(anyString())).thenReturn(p);when(p.executeUpdate()).thenReturn(1,0);DoctorDaoImpl dao=new DoctorDaoImpl();assertTrue(dao.updateDoctorRecord(c,doctor()));assertFalse(dao.updateDoctorRecord(c,doctor()));}
    @Test void statusRejectsInvalidBeforeSql() {assertThrows(IllegalArgumentException.class,()->new DoctorDaoImpl().updateDoctorStatus(mock(Connection.class),1L,"DELETED"));}
    @Test void duplicateAndBlockingQueriesReturnResultSetExistence()throws Exception{Connection c=mock(Connection.class);PreparedStatement p=mock(PreparedStatement.class);ResultSet r=mock(ResultSet.class);when(c.prepareStatement(anyString())).thenReturn(p);when(p.executeQuery()).thenReturn(r);when(r.next()).thenReturn(true,true,true);try(MockedStatic<DatabaseConnectionManager> m=mockStatic(DatabaseConnectionManager.class)){m.when(DatabaseConnectionManager::getConnection).thenReturn(c);DoctorDaoImpl dao=new DoctorDaoImpl();assertTrue(dao.existsByRegistrationNumber("R1"));assertTrue(dao.hasFutureAppointments(1L));assertTrue(dao.hasActiveSchedules(1L));}}
    @Test void paginationBindsOffsetSizeAndFilters()throws Exception{Connection c1=mock(Connection.class),c2=mock(Connection.class);PreparedStatement list=mock(PreparedStatement.class),count=mock(PreparedStatement.class);ResultSet rows=mock(ResultSet.class),total=mock(ResultSet.class);when(c1.prepareStatement(anyString())).thenReturn(list);when(c2.prepareStatement(anyString())).thenReturn(count);when(list.executeQuery()).thenReturn(rows);when(count.executeQuery()).thenReturn(total);when(total.next()).thenReturn(true);when(total.getLong(1)).thenReturn(0L);try(MockedStatic<DatabaseConnectionManager> m=mockStatic(DatabaseConnectionManager.class)){m.when(DatabaseConnectionManager::getConnection).thenReturn(c1,c2);new DoctorDaoImpl().findAll(new PageRequest(3,10,"heart%"),5L,"ACTIVE");}verify(list).setInt(18,20);verify(list).setInt(19,10);verify(list).setLong(14,5L);verify(list).setString(16,"ACTIVE");verify(list).setString(eq(2),contains("\\%"));}
    private Doctor doctor(){Doctor d=new Doctor();d.setDoctorId(2L);d.setUserId(3L);d.setDepartmentId(4L);d.setRegistrationNumber("REG-2");d.setFirstName("Asha");d.setLastName("Rao");d.setSpecialization("Medicine");d.setQualification("MD");d.setPhone("1234567890");d.setConsultationFee(new BigDecimal("500"));d.setStatus("ACTIVE");return d;}
}
