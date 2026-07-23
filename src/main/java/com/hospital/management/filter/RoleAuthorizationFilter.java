package com.hospital.management.filter;
import com.hospital.management.dao.AuditLogDao;import com.hospital.management.dao.impl.AuditLogDaoImpl;import com.hospital.management.model.AuthenticatedUser;import javax.servlet.*;import javax.servlet.annotation.WebFilter;import javax.servlet.http.*;import java.io.IOException;import java.util.*;
/** Explicit route-level role guard; services additionally enforce ownership. */
@WebFilter(urlPatterns={"/admin/*","/doctor/*","/nurse/*","/reception/*","/patient/*","/billing","/billing/*","/admissions","/admissions/*","/schedules","/schedules/*","/appointments","/appointments/*","/medical-records","/medical-records/*","/prescriptions","/prescriptions/*"})
public class RoleAuthorizationFilter implements Filter{
 private final AuditLogDao audit;public RoleAuthorizationFilter(){this(new AuditLogDaoImpl());}public RoleAuthorizationFilter(AuditLogDao a){audit=a;}
 public void doFilter(ServletRequest a,ServletResponse b,FilterChain c)throws IOException,ServletException{HttpServletRequest q=(HttpServletRequest)a;HttpServletResponse r=(HttpServletResponse)b;String path=q.getRequestURI().substring(q.getContextPath().length());HttpSession s=q.getSession(false);if(s==null||!(s.getAttribute("authenticatedUser") instanceof AuthenticatedUser u)){if(json(path))json(r,403,"Authentication required");else r.sendRedirect(q.getContextPath()+"/login");return;}if(!allowed(path,u.getRoleName())){try{audit.recordEvent("ACCESS_DENIED",u.getUserId(),"PATH",null,q.getRemoteAddr(),q.getHeader("User-Agent"));}catch(RuntimeException ignored){}if(json(path))json(r,403,"Access denied");else r.sendError(403);return;}c.doFilter(a,b);}
 private boolean allowed(String p,String role){
  if(p.equals("/admin/rooms")||p.equals("/admin/rooms/view"))return Set.of("ADMIN","BILLING_STAFF").contains(role);
  if(p.startsWith("/admin/rooms/"))return"ADMIN".equals(role);
  if(p.equals("/admin/beds")||p.equals("/admin/beds/view"))return Set.of("ADMIN","NURSE").contains(role);
  if(p.startsWith("/admin/beds/"))return"ADMIN".equals(role);
  if(p.equals("/admissions")||p.equals("/admissions/view"))return Set.of("ADMIN","RECEPTIONIST","DOCTOR","NURSE","PATIENT","BILLING_STAFF").contains(role);
  if(p.startsWith("/admissions/"))return Set.of("ADMIN","RECEPTIONIST").contains(role);
  if(p.equals("/billing/payment/refund"))return"ADMIN".equals(role);
  if(p.equals("/billing/edit")||p.equals("/billing/status"))return Set.of("ADMIN","BILLING_STAFF").contains(role);
  if(p.equals("/billing/create")||p.equals("/billing/payment/create"))return Set.of("ADMIN","BILLING_STAFF","RECEPTIONIST").contains(role);
  if(p.equals("/billing")||p.equals("/billing/view")||p.equals("/billing/print")||p.equals("/billing/payment/receipt"))return Set.of("ADMIN","BILLING_STAFF","RECEPTIONIST","PATIENT").contains(role);
  if(p.equals("/schedules")||p.startsWith("/schedules/"))return Set.of("ADMIN","DOCTOR").contains(role);
  if(p.equals("/admin/patients")||p.startsWith("/admin/patients/"))return Set.of("ADMIN","RECEPTIONIST").contains(role);
  if(p.startsWith("/admin/"))return"ADMIN".equals(role);
  if(p.equals("/appointments/create")||p.equals("/appointments/slots")||p.equals("/appointments/reschedule"))return Set.of("ADMIN","RECEPTIONIST","PATIENT").contains(role);
  if(p.equals("/appointments/edit"))return Set.of("ADMIN","RECEPTIONIST","DOCTOR").contains(role);
  if(p.equals("/appointments")||p.startsWith("/appointments/"))return Set.of("ADMIN","RECEPTIONIST","DOCTOR","PATIENT").contains(role);
  if(p.equals("/medical-records/create")||p.equals("/medical-records/edit"))return Set.of("ADMIN","DOCTOR").contains(role);
  if(p.equals("/medical-records")||p.equals("/medical-records/view"))return Set.of("ADMIN","DOCTOR","PATIENT").contains(role);
  if(p.equals("/prescriptions/create")||p.equals("/prescriptions/edit")||p.equals("/prescriptions/status"))return Set.of("ADMIN","DOCTOR").contains(role);
  if(p.equals("/prescriptions")||p.equals("/prescriptions/view")||p.equals("/prescriptions/print"))return Set.of("ADMIN","DOCTOR","PATIENT").contains(role);
  if(p.startsWith("/doctor/"))return Set.of("DOCTOR","ADMIN").contains(role);if(p.startsWith("/nurse/"))return Set.of("NURSE","ADMIN").contains(role);if(p.startsWith("/reception/"))return Set.of("RECEPTIONIST","ADMIN").contains(role);if(p.startsWith("/patient/"))return Set.of("PATIENT","ADMIN").contains(role);return true;
 }
 private boolean json(String p){return p.equals("/appointments/slots")||p.equals("/admissions/available-beds");}private void json(HttpServletResponse r,int code,String m)throws IOException{r.setStatus(code);r.setCharacterEncoding("UTF-8");r.setContentType("application/json");r.getWriter().write("{\"error\":\""+m+"\"}");}
}
