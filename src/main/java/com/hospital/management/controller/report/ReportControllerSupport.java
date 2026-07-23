package com.hospital.management.controller.report;

import com.hospital.management.model.report.ReportFilter;
import com.hospital.management.util.ReportRequestParser;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/** Shared safe parsing and view attributes for report controllers. */
final class ReportControllerSupport {
    private ReportControllerSupport(){}
    static ReportFilter filter(HttpServletRequest q){
        ReportRequestParser.ParseResult parsed=ReportRequestParser.parse(
                q.getParameter("dateFrom"),q.getParameter("dateTo"),q.getParameter("departmentId"),q.getParameter("doctorId"),
                q.getParameter("appointmentStatus"),q.getParameter("admissionStatus"),q.getParameter("billStatus"),q.getParameter("roomType"));
        q.setAttribute("filter",parsed.getFilter());q.setAttribute("errors",parsed.getErrors());
        q.setAttribute("generatedAt",LocalDateTime.now());return parsed.getFilter();
    }
}
