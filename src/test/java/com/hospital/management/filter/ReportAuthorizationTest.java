package com.hospital.management.filter;
import com.hospital.management.dao.AuditLogDao;import com.hospital.management.model.AuthenticatedUser;import org.junit.jupiter.api.Test;import javax.servlet.FilterChain;import javax.servlet.http.*;import static org.mockito.Mockito.*;
class ReportAuthorizationTest{
 private HttpServletRequest request(String role){HttpServletRequest q=mock(HttpServletRequest.class);HttpSession s=mock(HttpSession.class);when(q.getSession(false)).thenReturn(s);when(s.getAttribute("authenticatedUser")).thenReturn(new AuthenticatedUser(1L,"u","e",role));when(q.getContextPath()).thenReturn("/app");when(q.getRequestURI()).thenReturn("/app/admin/reports/export");return q;}
 @Test void adminAllowed()throws Exception{HttpServletRequest q=request("ADMIN");FilterChain c=mock(FilterChain.class);new RoleAuthorizationFilter(mock(AuditLogDao.class)).doFilter(q,mock(HttpServletResponse.class),c);verify(c).doFilter(eq(q),any());}
 @Test void everyNonAdminRoleDenied()throws Exception{for(String role:new String[]{"DOCTOR","PATIENT","RECEPTIONIST","BILLING_STAFF","NURSE"}){HttpServletResponse r=mock(HttpServletResponse.class);new RoleAuthorizationFilter(mock(AuditLogDao.class)).doFilter(request(role),r,mock(FilterChain.class));verify(r).sendError(403);}}
}
