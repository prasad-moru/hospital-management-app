package com.hospital.management.service;import com.hospital.management.model.AvailableSlot;import java.time.LocalDate;import java.util.List;
public interface AppointmentAvailabilityService {List<AvailableSlot> getAvailableSlots(Long doctorId,LocalDate appointmentDate,Long excludeAppointmentId);}
