package com.hospital.management.controller.report;
import com.hospital.management.model.report.ReportFilter;import com.hospital.management.service.*;import com.hospital.management.service.impl.ReportServiceImpl;
import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
/** Read-only patient registration analytics without private contact details. */
@WebServlet("/admin/reports/patients") public class PatientReportServlet extends HttpServlet{
 private final ReportService service;public PatientReportServlet(){this(new ReportServiceImpl());}public PatientReportServlet(ReportService s){service=s;}
 protected void doGet(HttpServletRequest q,HttpServletResponse r)throws ServletException,IOException{ReportFilter f=ReportControllerSupport.filter(q);q.setAttribute("trend",service.getPatientRegistrationTrend(f));q.setAttribute("statusSummary",service.getPatientStatusSummary(f));if("true".equals(q.getParameter("print")))q.setAttribute("printMode",true);q.getRequestDispatcher("/WEB-INF/views/reports/patients.jsp").forward(q,r);}
}
