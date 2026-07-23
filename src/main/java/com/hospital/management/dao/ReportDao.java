package com.hospital.management.dao;

import com.hospital.management.model.report.*;
import java.sql.SQLException;
import java.util.List;

/** Read-only aggregate access for the Reports and Analytics module. */
public interface ReportDao {
    HospitalOverviewReport getHospitalOverview(ReportFilter filter) throws SQLException;
    List<AppointmentStatusSummary> getAppointmentStatusSummary(ReportFilter filter) throws SQLException;
    List<AppointmentTrendPoint> getAppointmentTrend(ReportFilter filter) throws SQLException;
    List<PatientRegistrationSummary> getPatientRegistrationTrend(ReportFilter filter) throws SQLException;
    List<PatientStatusSummary> getPatientStatusSummary(ReportFilter filter) throws SQLException;
    List<DoctorDepartmentSummary> getDoctorDepartmentSummary(ReportFilter filter) throws SQLException;
    List<AdmissionSummary> getAdmissionStatusSummary(ReportFilter filter) throws SQLException;
    List<BedOccupancySummary> getBedOccupancySummary(ReportFilter filter) throws SQLException;
    List<BillingStatusSummary> getBillingStatusSummary(ReportFilter filter) throws SQLException;
    List<RevenueTrendPoint> getRevenueTrend(ReportFilter filter) throws SQLException;
}
