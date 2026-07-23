package com.hospital.management.model.report;

import java.math.BigDecimal;

/** Immutable high-level hospital metrics for a selected reporting period. */
public final class HospitalOverviewReport {
    private final long totalDepartments, activeDoctors, activePatients, totalAppointments;
    private final long activeAdmissions, availableBeds, occupiedBeds;
    private final BigDecimal totalBilledAmount, totalPaidAmount, outstandingAmount;

    public HospitalOverviewReport(long totalDepartments, long activeDoctors, long activePatients,
            long totalAppointments, long activeAdmissions, long availableBeds, long occupiedBeds,
            BigDecimal totalBilledAmount, BigDecimal totalPaidAmount, BigDecimal outstandingAmount) {
        this.totalDepartments = totalDepartments;
        this.activeDoctors = activeDoctors;
        this.activePatients = activePatients;
        this.totalAppointments = totalAppointments;
        this.activeAdmissions = activeAdmissions;
        this.availableBeds = availableBeds;
        this.occupiedBeds = occupiedBeds;
        this.totalBilledAmount = money(totalBilledAmount);
        this.totalPaidAmount = money(totalPaidAmount);
        this.outstandingAmount = money(outstandingAmount);
    }

    private static BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value.setScale(2);
    }
    public static HospitalOverviewReport empty() {
        return new HospitalOverviewReport(0, 0, 0, 0, 0, 0, 0, null, null, null);
    }
    public long getTotalDepartments() { return totalDepartments; }
    public long getActiveDoctors() { return activeDoctors; }
    public long getActivePatients() { return activePatients; }
    public long getTotalAppointments() { return totalAppointments; }
    public long getActiveAdmissions() { return activeAdmissions; }
    public long getAvailableBeds() { return availableBeds; }
    public long getOccupiedBeds() { return occupiedBeds; }
    public BigDecimal getTotalBilledAmount() { return totalBilledAmount; }
    public BigDecimal getTotalPaidAmount() { return totalPaidAmount; }
    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
}
