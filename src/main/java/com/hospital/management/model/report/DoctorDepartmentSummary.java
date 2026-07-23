package com.hospital.management.model.report;
/** Immutable doctor and appointment totals grouped by department. */
public final class DoctorDepartmentSummary {
    private final Long departmentId; private final String departmentName;
    private final long doctorCount, activeDoctorCount, appointmentCount;
    public DoctorDepartmentSummary(Long departmentId,String departmentName,long doctorCount,long activeDoctorCount,long appointmentCount){
        this.departmentId=departmentId;this.departmentName=departmentName;this.doctorCount=doctorCount;
        this.activeDoctorCount=activeDoctorCount;this.appointmentCount=appointmentCount;
    }
    public Long getDepartmentId(){return departmentId;} public String getDepartmentName(){return departmentName;}
    public long getDoctorCount(){return doctorCount;} public long getActiveDoctorCount(){return activeDoctorCount;}
    public long getAppointmentCount(){return appointmentCount;}
}
