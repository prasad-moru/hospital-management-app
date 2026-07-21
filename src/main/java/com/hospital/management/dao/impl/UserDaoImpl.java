package com.hospital.management.dao.impl;

import com.hospital.management.dao.UserDao;
import com.hospital.management.model.User;
import com.hospital.management.util.DatabaseConnectionManager;
import java.sql.*;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final String SELECT = "SELECT u.USER_ID,u.USERNAME,u.PASSWORD_HASH,u.EMAIL,u.ROLE_ID,r.ROLE_NAME,u.STATUS,u.FAILED_LOGIN_ATTEMPTS,u.LAST_LOGIN_AT,u.CREATED_AT,u.UPDATED_AT FROM USERS u JOIN ROLES r ON r.ROLE_ID=u.ROLE_ID ";
    public Optional<User> findByUsername(String value) throws SQLException { return find(SELECT+"WHERE UPPER(u.USERNAME)=UPPER(?)", value); }
    public Optional<User> findByEmail(String value) throws SQLException { return find(SELECT+"WHERE UPPER(u.EMAIL)=UPPER(?)", value); }
    public Optional<User> findById(Long value) throws SQLException { return find(SELECT+"WHERE u.USER_ID=?", value); }
    private Optional<User> find(String sql,Object value) throws SQLException {
        try(Connection c=DatabaseConnectionManager.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setObject(1,value); try(ResultSet r=p.executeQuery()){ return r.next()?Optional.of(map(r)):Optional.empty(); }
        }
    }
    public boolean usernameExists(String v)throws SQLException{return exists("SELECT 1 FROM USERS WHERE UPPER(USERNAME)=UPPER(?)",v);}
    public boolean emailExists(String v)throws SQLException{return exists("SELECT 1 FROM USERS WHERE UPPER(EMAIL)=UPPER(?)",v);}
    private boolean exists(String sql,String v)throws SQLException{try(Connection c=DatabaseConnectionManager.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,v);try(ResultSet r=p.executeQuery()){return r.next();}}}
    public long create(User u)throws SQLException{
        String sql="INSERT INTO USERS(USERNAME,PASSWORD_HASH,EMAIL,ROLE_ID,STATUS) VALUES(?,?,?,?,?)";
        try(Connection c=DatabaseConnectionManager.getConnection();PreparedStatement p=c.prepareStatement(sql,new String[]{"USER_ID"})){
            return executeCreate(p,u);
        }
    }
    public long create(Connection c,User u)throws SQLException{String sql="INSERT INTO USERS(USERNAME,PASSWORD_HASH,EMAIL,ROLE_ID,STATUS) VALUES(?,?,?,?,?)";try(PreparedStatement p=c.prepareStatement(sql,new String[]{"USER_ID"})){return executeCreate(p,u);}}
    private long executeCreate(PreparedStatement p,User u)throws SQLException{p.setString(1,u.getUsername());p.setString(2,u.getPasswordHash());p.setString(3,u.getEmail());p.setLong(4,u.getRoleId());p.setString(5,requiredStatus(u.getStatus()));p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){if(r.next())return r.getLong(1);}throw new SQLException("No generated user identifier returned");}
    public boolean updateUserIdentity(Connection c,Long id,String username,String email,String status)throws SQLException{try(PreparedStatement p=c.prepareStatement("UPDATE USERS SET USERNAME=?,EMAIL=?,STATUS=?,UPDATED_AT=CURRENT_TIMESTAMP WHERE USER_ID=?")){p.setString(1,username);p.setString(2,email);p.setString(3,requiredStatus(status));p.setLong(4,id);return p.executeUpdate()==1;}}
    public boolean updatePassword(Connection c,Long id,String hash)throws SQLException{if(hash==null||hash.isBlank())throw new IllegalArgumentException("Password hash is required");try(PreparedStatement p=c.prepareStatement("UPDATE USERS SET PASSWORD_HASH=?,UPDATED_AT=CURRENT_TIMESTAMP WHERE USER_ID=?")){p.setString(1,hash);p.setLong(2,id);return p.executeUpdate()==1;}}
    public boolean updateStatus(Connection c,Long id,String status)throws SQLException{try(PreparedStatement p=c.prepareStatement("UPDATE USERS SET STATUS=?,UPDATED_AT=CURRENT_TIMESTAMP WHERE USER_ID=?")){p.setString(1,requiredStatus(status));p.setLong(2,id);return p.executeUpdate()==1;}}
    public boolean usernameExistsExcludingUser(String value,Long id)throws SQLException{return existsExcluding("SELECT 1 FROM USERS WHERE UPPER(USERNAME)=UPPER(?) AND USER_ID<>? FETCH FIRST 1 ROW ONLY",value,id);}
    public boolean emailExistsExcludingUser(String value,Long id)throws SQLException{return existsExcluding("SELECT 1 FROM USERS WHERE UPPER(EMAIL)=UPPER(?) AND USER_ID<>? FETCH FIRST 1 ROW ONLY",value,id);}
    private boolean existsExcluding(String sql,String value,Long id)throws SQLException{if(value==null||value.isBlank()||id==null)return false;try(Connection c=DatabaseConnectionManager.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,value.trim());p.setLong(2,id);try(ResultSet r=p.executeQuery()){return r.next();}}}
    private String requiredStatus(String status){String normalized=status==null?"":status.trim().toUpperCase(java.util.Locale.ROOT);if(!java.util.Set.of("ACTIVE","INACTIVE","LOCKED").contains(normalized))throw new IllegalArgumentException("User status must be ACTIVE, INACTIVE or LOCKED");return normalized;}
    public void updateSuccessfulLogin(Long id)throws SQLException{update("UPDATE USERS SET LAST_LOGIN_AT=CURRENT_TIMESTAMP,FAILED_LOGIN_ATTEMPTS=0,UPDATED_AT=CURRENT_TIMESTAMP WHERE USER_ID=?",id);}
    public void incrementFailedLoginAttempts(Long id)throws SQLException{update("UPDATE USERS SET FAILED_LOGIN_ATTEMPTS=FAILED_LOGIN_ATTEMPTS+1,UPDATED_AT=CURRENT_TIMESTAMP WHERE USER_ID=?",id);}
    public void lockUser(Long id)throws SQLException{update("UPDATE USERS SET STATUS='LOCKED',UPDATED_AT=CURRENT_TIMESTAMP WHERE USER_ID=?",id);}
    public void resetFailedLoginAttempts(Long id)throws SQLException{update("UPDATE USERS SET FAILED_LOGIN_ATTEMPTS=0,UPDATED_AT=CURRENT_TIMESTAMP WHERE USER_ID=?",id);}
    private void update(String sql,Long id)throws SQLException{try(Connection c=DatabaseConnectionManager.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,id);p.executeUpdate();}}
    private User map(ResultSet r)throws SQLException{User u=new User();u.setUserId(r.getLong("USER_ID"));u.setUsername(r.getString("USERNAME"));u.setPasswordHash(r.getString("PASSWORD_HASH"));u.setEmail(r.getString("EMAIL"));u.setRoleId(r.getLong("ROLE_ID"));u.setRoleName(r.getString("ROLE_NAME"));u.setStatus(r.getString("STATUS"));u.setFailedLoginAttempts(r.getInt("FAILED_LOGIN_ATTEMPTS"));u.setLastLoginAt(time(r,"LAST_LOGIN_AT"));u.setCreatedAt(time(r,"CREATED_AT"));u.setUpdatedAt(time(r,"UPDATED_AT"));return u;}
    private java.time.LocalDateTime time(ResultSet r,String n)throws SQLException{Timestamp t=r.getTimestamp(n);return t==null?null:t.toLocalDateTime();}
}
