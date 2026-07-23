package com.hospital.management.model.report;
import java.time.LocalDate;
/** Immutable daily appointment trend point. */
public final class AppointmentTrendPoint {
    private final LocalDate reportDate; private final long appointmentCount;
    public AppointmentTrendPoint(LocalDate reportDate,long appointmentCount){this.reportDate=reportDate;this.appointmentCount=appointmentCount;}
    public LocalDate getReportDate(){return reportDate;} public long getAppointmentCount(){return appointmentCount;}
}
