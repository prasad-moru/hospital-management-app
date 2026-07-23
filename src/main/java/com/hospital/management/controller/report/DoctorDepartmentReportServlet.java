package com.hospital.management.controller.report;
import com.hospital.management.model.report.ReportFilter;import com.hospital.management.service.*;import com.hospital.management.service.impl.ReportServiceImpl;
import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
/** Read-only doctor and department aggregate report. */
@WebServlet("/admin/reports/doctors") public class DoctorDepartmentReportServlet extends HttpServlet{
 private final ReportService service;public DoctorDepartmentReportServlet(){this(new ReportServiceImpl());}public DoctorDepartmentReportServlet(ReportService s){service=s;}
 protected void doGet(HttpServletRequest q,HttpServletResponse r)throws ServletException,IOException{ReportFilter f=ReportControllerSupport.filter(q);q.setAttribute("summary",service.getDoctorDepartmentSummary(f));if("true".equals(q.getParameter("print")))q.setAttribute("printMode",true);q.getRequestDispatcher("/WEB-INF/views/reports/doctors.jsp").forward(q,r);}
}
