package com.hospital.management.service;
import com.hospital.management.model.AuthenticatedUser;
public final class AuthenticationResult {
 public enum Status {SUCCESS,INVALID_CREDENTIALS,ACCOUNT_LOCKED,ACCOUNT_INACTIVE,VALIDATION_ERROR,SYSTEM_ERROR}
 private final Status status; private final String message; private final AuthenticatedUser user;
 private AuthenticationResult(Status s,String m,AuthenticatedUser u){status=s;message=m;user=u;}
 public static AuthenticationResult of(Status s,String m){return new AuthenticationResult(s,m,null);}
 public static AuthenticationResult success(AuthenticatedUser u){return new AuthenticationResult(Status.SUCCESS,"Login successful",u);}
 public Status getStatus(){return status;} public String getMessage(){return message;} public AuthenticatedUser getUser(){return user;} public boolean isSuccess(){return status==Status.SUCCESS;}
}
