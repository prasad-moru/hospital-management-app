package com.hospital.management.util;
import javax.servlet.http.HttpSession;import java.security.SecureRandom;import java.util.Base64;
/** Session-bound cryptographically secure CSRF tokens. */
public final class CsrfTokenUtil {public static final String SESSION_ATTRIBUTE="csrfToken";private static final SecureRandom RANDOM=new SecureRandom();private CsrfTokenUtil(){throw new IllegalStateException("Utility class");}
 public static String getOrCreateToken(HttpSession s){Object old=s.getAttribute(SESSION_ATTRIBUTE);if(old instanceof String token&&!token.isBlank())return token;byte[] b=new byte[32];RANDOM.nextBytes(b);String t=Base64.getUrlEncoder().withoutPadding().encodeToString(b);s.setAttribute(SESSION_ATTRIBUTE,t);return t;}
 public static boolean validate(HttpSession s,String supplied){if(s==null||supplied==null)return false;Object expected=s.getAttribute(SESSION_ATTRIBUTE);return expected instanceof String e&&constantTime(e,supplied);}
 private static boolean constantTime(String a,String b){byte[] x=a.getBytes(java.nio.charset.StandardCharsets.UTF_8),y=b.getBytes(java.nio.charset.StandardCharsets.UTF_8);return java.security.MessageDigest.isEqual(x,y);}}
