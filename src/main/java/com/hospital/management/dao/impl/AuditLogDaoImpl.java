package com.hospital.management.dao.impl;
import com.hospital.management.dao.AuditLogDao;
import com.hospital.management.util.DatabaseConnectionManager;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import java.sql.*;
public class AuditLogDaoImpl implements AuditLogDao {
 private static final Logger LOG=LoggerFactory.getLogger(AuditLogDaoImpl.class);
 public void recordEvent(String a,Long uid,String e,Long eid,String ip,String ua){String sql="INSERT INTO AUDIT_LOGS(USER_ID,ACTION,ENTITY_NAME,ENTITY_ID,IP_ADDRESS,USER_AGENT) VALUES(?,?,?,?,?,?)";try(Connection c=DatabaseConnectionManager.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(uid==null)p.setNull(1,Types.NUMERIC);else p.setLong(1,uid);p.setString(2,a);p.setString(3,e);if(eid==null)p.setNull(4,Types.NUMERIC);else p.setLong(4,eid);p.setString(5,ip);p.setString(6,ua);p.executeUpdate();}catch(SQLException|RuntimeException ex){LOG.warn("Unable to record audit event {}",a,ex);}}
}
