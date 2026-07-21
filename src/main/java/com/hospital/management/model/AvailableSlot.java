package com.hospital.management.model;
import java.time.LocalTime;
/** Immutable appointment slot safe for JSP and JSON rendering. */
public record AvailableSlot(LocalTime startTime, LocalTime endTime, int durationMinutes) {
    public AvailableSlot { if(startTime==null||endTime==null||durationMinutes<=0) throw new IllegalArgumentException("Invalid slot"); }
    public LocalTime getStartTime(){return startTime;} public LocalTime getEndTime(){return endTime;} public int getDurationMinutes(){return durationMinutes;}
}
