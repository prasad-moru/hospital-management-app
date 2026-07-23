package com.hospital.management.controller.report;
import com.hospital.management.model.report.ReportFilter;import com.hospital.management.service.*;import com.hospital.management.service.impl.ReportServiceImpl;
import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
/** Read-only admission status and operational-bed occupancy report. */
@WebServlet("/admin/reports/admissions") public class AdmissionOccupancyReportServlet extends HttpServlet{
 private final ReportService service;public AdmissionOccupancyReportServlet(){this(new ReportServiceImpl());}public AdmissionOccupancyReportServlet(ReportService s){service=s;}
 protected void doGet(HttpServletRequest q,HttpServletResponse r)throws ServletException,IOException{ReportFilter f=ReportControllerSupport.filter(q);q.setAttribute("admissionSummary",service.getAdmissionStatusSummary(f));q.setAttribute("bedSummary",service.getBedOccupancySummary(f));if("true".equals(q.getParameter("print")))q.setAttribute("printMode",true);q.getRequestDispatcher("/WEB-INF/views/reports/admissions.jsp").forward(q,r);}
}
