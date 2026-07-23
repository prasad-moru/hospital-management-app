package com.hospital.management.model.report;
/** Immutable appointment count grouped by status. */
public final class AppointmentStatusSummary {
    private final String status; private final long appointmentCount;
    public AppointmentStatusSummary(String status, long appointmentCount) { this.status=status; this.appointmentCount=appointmentCount; }
    public String getStatus(){return status;} public long getAppointmentCount(){return appointmentCount;}
}
