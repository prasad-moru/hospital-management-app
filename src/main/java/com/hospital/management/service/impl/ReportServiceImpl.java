package com.hospital.management.service.impl;

import com.hospital.management.dao.ReportDao;
import com.hospital.management.dao.impl.ReportDaoImpl;
import com.hospital.management.model.report.*;
import com.hospital.management.service.ReportService;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Converts report SQL failures into safe empty read-only report results. */
public class ReportServiceImpl implements ReportService {
    private static final Logger LOGGER=LoggerFactory.getLogger(ReportServiceImpl.class);
    private final ReportDao dao;
    public ReportServiceImpl(){this(new ReportDaoImpl());}
    public ReportServiceImpl(ReportDao dao){this.dao=dao;}
    public HospitalOverviewReport getHospitalOverview(ReportFilter f){try{return valid(f)?dao.getHospitalOverview(f):HospitalOverviewReport.empty();}catch(SQLException e){log(e);return HospitalOverviewReport.empty();}}
    public List<AppointmentStatusSummary> getAppointmentStatusSummary(ReportFilter f){try{return valid(f)?safe(dao.getAppointmentStatusSummary(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<AppointmentTrendPoint> getAppointmentTrend(ReportFilter f){try{return valid(f)?safe(dao.getAppointmentTrend(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<PatientRegistrationSummary> getPatientRegistrationTrend(ReportFilter f){try{return valid(f)?safe(dao.getPatientRegistrationTrend(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<PatientStatusSummary> getPatientStatusSummary(ReportFilter f){try{return valid(f)?safe(dao.getPatientStatusSummary(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<DoctorDepartmentSummary> getDoctorDepartmentSummary(ReportFilter f){try{return valid(f)?safe(dao.getDoctorDepartmentSummary(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<AdmissionSummary> getAdmissionStatusSummary(ReportFilter f){try{return valid(f)?safe(dao.getAdmissionStatusSummary(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<BedOccupancySummary> getBedOccupancySummary(ReportFilter f){try{return valid(f)?safe(dao.getBedOccupancySummary(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<BillingStatusSummary> getBillingStatusSummary(ReportFilter f){try{return valid(f)?safe(dao.getBillingStatusSummary(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    public List<RevenueTrendPoint> getRevenueTrend(ReportFilter f){try{return valid(f)?safe(dao.getRevenueTrend(f)):List.of();}catch(SQLException e){log(e);return List.of();}}
    private boolean valid(ReportFilter f){return f!=null&&f.getDateFrom()!=null&&f.getDateTo()!=null&&!f.getDateFrom().isAfter(f.getDateTo());}
    private <T> List<T> safe(List<T> values){return values==null?List.of():values;}
    private void log(SQLException e){LOGGER.error("Read-only report query failed",e);}
}
