package com.hospital.management.controller.report;
import com.hospital.management.model.report.ReportFilter;import com.hospital.management.service.*;import com.hospital.management.service.impl.ReportServiceImpl;
import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
/** Read-only billing status and daily revenue report. */
@WebServlet("/admin/reports/billing") public class BillingRevenueReportServlet extends HttpServlet{
 private final ReportService service;public BillingRevenueReportServlet(){this(new ReportServiceImpl());}public BillingRevenueReportServlet(ReportService s){service=s;}
 protected void doGet(HttpServletRequest q,HttpServletResponse r)throws ServletException,IOException{ReportFilter f=ReportControllerSupport.filter(q);q.setAttribute("summary",service.getBillingStatusSummary(f));q.setAttribute("trend",service.getRevenueTrend(f));if("true".equals(q.getParameter("print")))q.setAttribute("printMode",true);q.getRequestDispatcher("/WEB-INF/views/reports/billing.jsp").forward(q,r);}
}
