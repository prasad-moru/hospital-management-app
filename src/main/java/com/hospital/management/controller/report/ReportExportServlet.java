package com.hospital.management.controller.report;

import com.hospital.management.dao.AuditLogDao;
import com.hospital.management.dao.impl.AuditLogDaoImpl;
import com.hospital.management.model.AuthenticatedUser;
import com.hospital.management.model.report.*;
import com.hospital.management.service.ReportService;
import com.hospital.management.service.impl.ReportServiceImpl;
import com.hospital.management.util.CsvUtil;
import com.hospital.management.util.ReportRequestParser;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/** ADMIN-only, formula-safe CSV export for allow-listed aggregate reports. */
@WebServlet("/admin/reports/export")
public class ReportExportServlet extends HttpServlet {
    private static final Set<String> TYPES=Set.of("appointments","patients","doctors","admissions","beds","billing","revenue");
    private static final Map<String,Long> TYPE_IDS=Map.of("appointments",1L,"patients",2L,"doctors",3L,"admissions",4L,"beds",5L,"billing",6L,"revenue",7L);
    private final ReportService service;private final AuditLogDao audit;
    public ReportExportServlet(){this(new ReportServiceImpl(),new AuditLogDaoImpl());}
    public ReportExportServlet(ReportService service,AuditLogDao audit){this.service=service;this.audit=audit;}
    protected void doGet(HttpServletRequest q,HttpServletResponse r)throws IOException{
        String type=q.getParameter("type");
        if(type==null||!TYPES.contains(type)){r.sendError(400);return;}
        ReportRequestParser.ParseResult parsed=ReportRequestParser.parse(q.getParameter("dateFrom"),q.getParameter("dateTo"),
                q.getParameter("departmentId"),q.getParameter("doctorId"),q.getParameter("appointmentStatus"),
                q.getParameter("admissionStatus"),q.getParameter("billStatus"),q.getParameter("roomType"));
        if(!parsed.isValid()){r.sendError(400);return;}
        r.setCharacterEncoding("UTF-8");r.setContentType("text/csv; charset=UTF-8");
        r.setHeader("Content-Disposition","attachment; filename=\"hospital-"+type+"-report.csv\"");
        write(type,parsed.getFilter(),r.getWriter());
        HttpSession session=q.getSession(false);Object value=session==null?null:session.getAttribute("authenticatedUser");
        Long userId=value instanceof AuthenticatedUser user?user.getUserId():null;
        audit.recordEvent("REPORT_EXPORTED",userId,"REPORT",TYPE_IDS.get(type),q.getRemoteAddr(),q.getHeader("User-Agent"));
    }
    private void write(String type,ReportFilter f,PrintWriter out){
        switch(type){
            case "appointments"->{out.print(CsvUtil.row("Status","Appointment Count"));for(var x:service.getAppointmentStatusSummary(f))out.print(CsvUtil.row(x.getStatus(),x.getAppointmentCount()));}
            case "patients"->{out.print(CsvUtil.row("Registration Date","Patient Count"));for(var x:service.getPatientRegistrationTrend(f))out.print(CsvUtil.row(x.getReportDate(),x.getPatientCount()));}
            case "doctors"->{out.print(CsvUtil.row("Department ID","Department","Doctors","Active Doctors","Appointments"));for(var x:service.getDoctorDepartmentSummary(f))out.print(CsvUtil.row(x.getDepartmentId(),x.getDepartmentName(),x.getDoctorCount(),x.getActiveDoctorCount(),x.getAppointmentCount()));}
            case "admissions"->{out.print(CsvUtil.row("Status","Admission Count"));for(var x:service.getAdmissionStatusSummary(f))out.print(CsvUtil.row(x.getStatus(),x.getAdmissionCount()));}
            case "beds"->{out.print(CsvUtil.row("Room Type","Operational Beds","Available","Occupied","Maintenance","Occupancy Percentage"));for(var x:service.getBedOccupancySummary(f))out.print(CsvUtil.row(x.getRoomType(),x.getTotalBeds(),x.getAvailableBeds(),x.getOccupiedBeds(),x.getMaintenanceBeds(),x.getOccupancyPercentage()));}
            case "billing"->{out.print(CsvUtil.row("Status","Bills","Total Amount","Paid Amount","Balance Amount"));for(var x:service.getBillingStatusSummary(f))out.print(CsvUtil.row(x.getStatus(),x.getBillCount(),x.getTotalAmount(),x.getPaidAmount(),x.getBalanceAmount()));}
            case "revenue"->{out.print(CsvUtil.row("Date","Billed Amount","Successful Payments","Refunded Amount"));for(var x:service.getRevenueTrend(f))out.print(CsvUtil.row(x.getReportDate(),x.getBilledAmount(),x.getPaidAmount(),x.getRefundedAmount()));}
            default->throw new IllegalArgumentException("Unsupported report type");
        }
    }
}
