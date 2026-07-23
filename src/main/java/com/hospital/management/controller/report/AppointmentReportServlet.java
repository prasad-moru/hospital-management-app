package com.hospital.management.controller.report;
import com.hospital.management.model.report.ReportFilter;import com.hospital.management.service.*;import com.hospital.management.service.impl.ReportServiceImpl;
import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
/** Read-only appointment analytics. */
@WebServlet("/admin/reports/appointments") public class AppointmentReportServlet extends HttpServlet{
 private final ReportService service;public AppointmentReportServlet(){this(new ReportServiceImpl());}public AppointmentReportServlet(ReportService s){service=s;}
 protected void doGet(HttpServletRequest q,HttpServletResponse r)throws ServletException,IOException{ReportFilter f=ReportControllerSupport.filter(q);q.setAttribute("summary",service.getAppointmentStatusSummary(f));q.setAttribute("trend",service.getAppointmentTrend(f));q.setAttribute("doctorSummary",service.getDoctorDepartmentSummary(f));if("true".equals(q.getParameter("print")))q.setAttribute("printMode",true);q.getRequestDispatcher("/WEB-INF/views/reports/appointments.jsp").forward(q,r);}
}
