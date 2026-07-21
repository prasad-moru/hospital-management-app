package com.hospital.management.util;import java.sql.*;import java.time.Year;
/** Generates collision-resistant Oracle sequence-backed patient numbers. */
public final class PatientNumberGenerator{private PatientNumberGenerator(){}public static String next()throws SQLException{try(Connection c=DatabaseConnectionManager.getConnection();PreparedStatement s=c.prepareStatement("SELECT PATIENT_NUMBER_SEQ.NEXTVAL FROM DUAL");ResultSet r=s.executeQuery()){r.next();return String.format("PAT-%d-%06d",Year.now().getValue(),r.getLong(1));}}}
