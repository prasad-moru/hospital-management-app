package com.hospital.management.service.impl;
import com.hospital.management.dao.*;import com.hospital.management.dao.impl.*;import com.hospital.management.model.*;import com.hospital.management.service.*;import com.hospital.management.util.PasswordUtil;import org.slf4j.*;import java.sql.SQLException;import java.util.Optional;
public class AuthenticationServiceImpl implements AuthenticationService {
 private static final Logger LOG=LoggerFactory.getLogger(AuthenticationServiceImpl.class);private static final int LIMIT=5;private final UserDao users;private final AuditLogDao audit;
 public AuthenticationServiceImpl(){this(new UserDaoImpl(),new AuditLogDaoImpl());}
 public AuthenticationServiceImpl(UserDao users,AuditLogDao audit){this.users=users;this.audit=audit;}
 public AuthenticationResult authenticate(String username,String password){
  if(username==null||username.trim().isEmpty()||password==null||password.isBlank())return AuthenticationResult.of(AuthenticationResult.Status.VALIDATION_ERROR,"Username and password are required");
  String name=username.trim();try{Optional<User> found=users.findByUsername(name);if(found.isEmpty()){audit.recordEvent("LOGIN_FAILED",null,"USER",null,null,null);return invalid();}User u=found.get();
   if("LOCKED".equals(u.getStatus()))return AuthenticationResult.of(AuthenticationResult.Status.ACCOUNT_LOCKED,"Account is locked. Contact an administrator");
   if(!"ACTIVE".equals(u.getStatus()))return AuthenticationResult.of(AuthenticationResult.Status.ACCOUNT_INACTIVE,"Account is inactive. Contact an administrator");
   if(!PasswordUtil.verifyPassword(password,u.getPasswordHash())){users.incrementFailedLoginAttempts(u.getUserId());if(u.getFailedLoginAttempts()+1>=LIMIT){users.lockUser(u.getUserId());audit.recordEvent("ACCOUNT_LOCKED",u.getUserId(),"USER",u.getUserId(),null,null);}else audit.recordEvent("LOGIN_FAILED",u.getUserId(),"USER",u.getUserId(),null,null);return invalid();}
   users.resetFailedLoginAttempts(u.getUserId());users.updateSuccessfulLogin(u.getUserId());audit.recordEvent("LOGIN_SUCCESS",u.getUserId(),"USER",u.getUserId(),null,null);return AuthenticationResult.success(new AuthenticatedUser(u.getUserId(),u.getUsername(),u.getEmail(),u.getRoleName()));
  }catch(SQLException ex){LOG.error("Authentication database operation failed",ex);return AuthenticationResult.of(AuthenticationResult.Status.SYSTEM_ERROR,"Authentication service is temporarily unavailable");}}
 private AuthenticationResult invalid(){return AuthenticationResult.of(AuthenticationResult.Status.INVALID_CREDENTIALS,"Invalid username or password");}
}
