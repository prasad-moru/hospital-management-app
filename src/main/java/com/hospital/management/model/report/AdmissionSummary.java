package com.hospital.management.model.report;
/** Immutable admission count grouped by lifecycle status. */
public final class AdmissionSummary {
    private final String status; private final long admissionCount;
    public AdmissionSummary(String status,long admissionCount){this.status=status;this.admissionCount=admissionCount;}
    public String getStatus(){return status;} public long getAdmissionCount(){return admissionCount;}
}
