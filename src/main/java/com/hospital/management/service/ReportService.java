package com.hospital.management.service;
import com.hospital.management.model.report.*;import java.util.List;
/** Read-only report orchestration contract. */
public interface ReportService {
    HospitalOverviewReport getHospitalOverview(ReportFilter filter);
    List<AppointmentStatusSummary> getAppointmentStatusSummary(ReportFilter filter);
    List<AppointmentTrendPoint> getAppointmentTrend(ReportFilter filter);
    List<PatientRegistrationSummary> getPatientRegistrationTrend(ReportFilter filter);
    List<PatientStatusSummary> getPatientStatusSummary(ReportFilter filter);
    List<DoctorDepartmentSummary> getDoctorDepartmentSummary(ReportFilter filter);
    List<AdmissionSummary> getAdmissionStatusSummary(ReportFilter filter);
    List<BedOccupancySummary> getBedOccupancySummary(ReportFilter filter);
    List<BillingStatusSummary> getBillingStatusSummary(ReportFilter filter);
    List<RevenueTrendPoint> getRevenueTrend(ReportFilter filter);
}
