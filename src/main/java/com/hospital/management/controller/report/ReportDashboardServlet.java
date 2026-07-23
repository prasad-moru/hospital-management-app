package com.hospital.management.controller.report;
import com.hospital.management.model.report.ReportFilter;import com.hospital.management.service.*;import com.hospital.management.service.impl.ReportServiceImpl;
import javax.servlet.*;import javax.servlet.annotation.WebServlet;import javax.servlet.http.*;import java.io.IOException;
/** ADMIN-only Reports and Analytics dashboard. */
@WebServlet("/admin/reports")
public class ReportDashboardServlet extends HttpServlet{
    private final ReportService service;public ReportDashboardServlet(){this(new ReportServiceImpl());}public ReportDashboardServlet(ReportService service){this.service=service;}
    protected void doGet(HttpServletRequest q,HttpServletResponse r)throws ServletException,IOException{
        ReportFilter f=ReportControllerSupport.filter(q);
        q.setAttribute("overview",service.getHospitalOverview(f));q.setAttribute("appointmentSummary",service.getAppointmentStatusSummary(f));
        q.setAttribute("doctorSummary",service.getDoctorDepartmentSummary(f));q.setAttribute("admissionSummary",service.getAdmissionStatusSummary(f));
        q.setAttribute("bedSummary",service.getBedOccupancySummary(f));q.setAttribute("billingSummary",service.getBillingStatusSummary(f));
        q.getRequestDispatcher("/WEB-INF/views/reports/dashboard.jsp").forward(q,r);
    }
}
