package com.hospital.management.model.report;
/** Immutable patient count grouped by patient status. */
public final class PatientStatusSummary {
    private final String status;private final long patientCount;
    public PatientStatusSummary(String status,long patientCount){this.status=status;this.patientCount=patientCount;}
    public String getStatus(){return status;}public long getPatientCount(){return patientCount;}
}
