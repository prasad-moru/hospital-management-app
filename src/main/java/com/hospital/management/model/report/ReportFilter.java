package com.hospital.management.model.report;
import java.time.LocalDate;import java.util.Locale;
/** Immutable, normalized report filters independent of the servlet API. */
public final class ReportFilter {
    private final LocalDate dateFrom,dateTo; private final Long departmentId,doctorId;
    private final String appointmentStatus,admissionStatus,billStatus,roomType;
    public ReportFilter(LocalDate dateFrom,LocalDate dateTo,Long departmentId,Long doctorId,
            String appointmentStatus,String admissionStatus,String billStatus,String roomType){
        this.dateFrom=dateFrom;this.dateTo=dateTo;this.departmentId=positive(departmentId);this.doctorId=positive(doctorId);
        this.appointmentStatus=normalize(appointmentStatus);this.admissionStatus=normalize(admissionStatus);
        this.billStatus=normalize(billStatus);this.roomType=normalize(roomType);
    }
    private static Long positive(Long v){return v!=null&&v>0?v:null;}
    private static String normalize(String v){return v==null||v.isBlank()?null:v.trim().toUpperCase(Locale.ROOT);}
    public LocalDate getDateFrom(){return dateFrom;} public LocalDate getDateTo(){return dateTo;}
    public Long getDepartmentId(){return departmentId;} public Long getDoctorId(){return doctorId;}
    public String getAppointmentStatus(){return appointmentStatus;} public String getAdmissionStatus(){return admissionStatus;}
    public String getBillStatus(){return billStatus;} public String getRoomType(){return roomType;}
}
