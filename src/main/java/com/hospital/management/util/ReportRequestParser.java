package com.hospital.management.util;

import com.hospital.management.model.report.ReportFilter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Parses and validates allow-listed report filters without accepting SQL fragments. */
public final class ReportRequestParser {
    private ReportRequestParser(){}

    public static ParseResult parse(String from,String to,String department,String doctor,
            String appointmentStatus,String admissionStatus,String billStatus,String roomType){
        Map<String,String> errors=new LinkedHashMap<>();
        LocalDate today=LocalDate.now();
        LocalDate dateFrom=date(from,"dateFrom",today.withDayOfMonth(1),errors);
        LocalDate dateTo=date(to,"dateTo",today,errors);
        Long departmentId=id(department,"departmentId",errors);
        Long doctorId=id(doctor,"doctorId",errors);
        if(dateFrom!=null&&dateTo!=null&&dateFrom.isAfter(dateTo))errors.put("dateRange","Start date must not be after end date");
        return new ParseResult(new ReportFilter(dateFrom,dateTo,departmentId,doctorId,
                allowed(appointmentStatus,"appointmentStatus",errors,"SCHEDULED","CONFIRMED","COMPLETED","CANCELLED","NO_SHOW"),
                allowed(admissionStatus,"admissionStatus",errors,"ADMITTED","TRANSFERRED","DISCHARGED","CANCELLED"),
                allowed(billStatus,"billStatus",errors,"UNPAID","PARTIALLY_PAID","PAID","CANCELLED","REFUNDED"),
                allowed(roomType,"roomType",errors,"GENERAL","SEMI_PRIVATE","PRIVATE","ICU","EMERGENCY","MATERNITY","PEDIATRIC","OTHER")),errors);
    }
    private static LocalDate date(String value,String field,LocalDate fallback,Map<String,String> errors){
        if(value==null||value.isBlank())return fallback;
        try{return LocalDate.parse(value);}catch(DateTimeParseException e){errors.put(field,"Use yyyy-MM-dd");return fallback;}
    }
    private static Long id(String value,String field,Map<String,String> errors){
        if(value==null||value.isBlank())return null;
        try{long id=Long.parseLong(value);if(id>0)return id;}catch(NumberFormatException ignored){}
        errors.put(field,"Enter a positive numeric ID");return null;
    }
    private static String allowed(String value,String field,Map<String,String> errors,String... allowed){
        if(value==null||value.isBlank())return null;String normalized=value.trim().toUpperCase(java.util.Locale.ROOT);
        for(String candidate:allowed)if(candidate.equals(normalized))return normalized;
        errors.put(field,"Unsupported filter value");return null;
    }
    public static final class ParseResult{
        private final ReportFilter filter;private final Map<String,String> errors;
        private ParseResult(ReportFilter filter,Map<String,String> errors){this.filter=filter;this.errors=Collections.unmodifiableMap(new LinkedHashMap<>(errors));}
        public ReportFilter getFilter(){return filter;}public Map<String,String> getErrors(){return errors;}public boolean isValid(){return errors.isEmpty();}
    }
}
