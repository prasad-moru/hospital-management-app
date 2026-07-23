package com.hospital.management.model.report;
import java.time.LocalDate;
/** Immutable daily patient-registration trend point. */
public final class PatientRegistrationSummary {
    private final LocalDate reportDate; private final long patientCount;
    public PatientRegistrationSummary(LocalDate reportDate,long patientCount){this.reportDate=reportDate;this.patientCount=patientCount;}
    public LocalDate getReportDate(){return reportDate;} public long getPatientCount(){return patientCount;}
}
