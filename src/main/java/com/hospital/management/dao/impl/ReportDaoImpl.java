package com.hospital.management.dao.impl;

import com.hospital.management.dao.ReportDao;
import com.hospital.management.model.report.*;
import com.hospital.management.util.DatabaseConnectionManager;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Oracle-compatible, SELECT-only report queries using bound filter values. */
public class ReportDaoImpl implements ReportDao {
    @Override
    public HospitalOverviewReport getHospitalOverview(ReportFilter f) throws SQLException {
        String sql = """
            SELECT
              (SELECT COUNT(*) FROM DEPARTMENTS),
              (SELECT COUNT(*) FROM DOCTORS d JOIN USERS u ON u.USER_ID=d.USER_ID
                WHERE d.STATUS='ACTIVE' AND u.STATUS='ACTIVE'),
              (SELECT COUNT(*) FROM PATIENTS WHERE STATUS='ACTIVE'),
              (SELECT COUNT(*) FROM APPOINTMENTS a JOIN DOCTORS d ON d.DOCTOR_ID=a.DOCTOR_ID
                WHERE a.APPOINTMENT_DATE>=? AND a.APPOINTMENT_DATE<?
                  AND (? IS NULL OR d.DEPARTMENT_ID=?) AND (? IS NULL OR a.DOCTOR_ID=?)),
              (SELECT COUNT(*) FROM ADMISSIONS WHERE STATUS IN ('ADMITTED','TRANSFERRED')),
              (SELECT COUNT(*) FROM BEDS b JOIN ROOMS r ON r.ROOM_ID=b.ROOM_ID
                WHERE b.STATUS='AVAILABLE' AND r.STATUS='ACTIVE'),
              (SELECT COUNT(*) FROM BEDS WHERE STATUS='OCCUPIED'),
              (SELECT NVL(SUM(TOTAL_AMOUNT),0) FROM BILLS
                WHERE BILL_DATE>=? AND BILL_DATE<? AND STATUS<>'CANCELLED'),
              (SELECT NVL(SUM(AMOUNT),0) FROM PAYMENTS
                WHERE PAYMENT_DATE>=? AND PAYMENT_DATE<? AND STATUS='SUCCESS'),
              (SELECT NVL(SUM(BALANCE_AMOUNT),0) FROM BILLS
                WHERE BILL_DATE>=? AND BILL_DATE<? AND STATUS NOT IN ('CANCELLED','REFUNDED'))
            FROM DUAL
            """;
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=1;i=dates(p,i,f);i=nullablePair(p,i,f.getDepartmentId());i=nullablePair(p,i,f.getDoctorId());
            i=dates(p,i,f);i=dates(p,i,f);dates(p,i,f);
            try(ResultSet r=p.executeQuery()){r.next();return new HospitalOverviewReport(
                    r.getLong(1),r.getLong(2),r.getLong(3),r.getLong(4),r.getLong(5),r.getLong(6),r.getLong(7),
                    money(r,8),money(r,9),money(r,10));}
        }
    }

    @Override
    public List<AppointmentStatusSummary> getAppointmentStatusSummary(ReportFilter f)throws SQLException{
        String sql="""
            SELECT a.STATUS,COUNT(*)
            FROM APPOINTMENTS a JOIN DOCTORS d ON d.DOCTOR_ID=a.DOCTOR_ID
            WHERE a.APPOINTMENT_DATE>=? AND a.APPOINTMENT_DATE<?
              AND (? IS NULL OR d.DEPARTMENT_ID=?)
              AND (? IS NULL OR a.DOCTOR_ID=?)
              AND (? IS NULL OR a.STATUS=?)
            GROUP BY a.STATUS ORDER BY a.STATUS
            """;
        List<AppointmentStatusSummary> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=dates(p,1,f);i=nullablePair(p,i,f.getDepartmentId());i=nullablePair(p,i,f.getDoctorId());textPair(p,i,f.getAppointmentStatus());
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new AppointmentStatusSummary(r.getString(1),r.getLong(2)));}
        }return out;
    }

    @Override
    public List<AppointmentTrendPoint> getAppointmentTrend(ReportFilter f)throws SQLException{
        String sql="""
            SELECT TRUNC(a.APPOINTMENT_DATE),COUNT(*)
            FROM APPOINTMENTS a JOIN DOCTORS d ON d.DOCTOR_ID=a.DOCTOR_ID
            WHERE a.APPOINTMENT_DATE>=? AND a.APPOINTMENT_DATE<?
              AND (? IS NULL OR d.DEPARTMENT_ID=?) AND (? IS NULL OR a.DOCTOR_ID=?)
              AND (? IS NULL OR a.STATUS=?)
            GROUP BY TRUNC(a.APPOINTMENT_DATE) ORDER BY TRUNC(a.APPOINTMENT_DATE)
            """;
        List<AppointmentTrendPoint> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=dates(p,1,f);i=nullablePair(p,i,f.getDepartmentId());i=nullablePair(p,i,f.getDoctorId());textPair(p,i,f.getAppointmentStatus());
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new AppointmentTrendPoint(r.getDate(1).toLocalDate(),r.getLong(2)));}
        }return out;
    }

    @Override
    public List<PatientRegistrationSummary> getPatientRegistrationTrend(ReportFilter f)throws SQLException{
        String sql="""
            SELECT TRUNC(CREATED_AT),COUNT(*) FROM PATIENTS
            WHERE CREATED_AT>=? AND CREATED_AT<?
            GROUP BY TRUNC(CREATED_AT) ORDER BY TRUNC(CREATED_AT)
            """;
        List<PatientRegistrationSummary> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            dates(p,1,f);try(ResultSet r=p.executeQuery()){while(r.next())out.add(new PatientRegistrationSummary(r.getDate(1).toLocalDate(),r.getLong(2)));}
        }return out;
    }

    @Override
    public List<PatientStatusSummary> getPatientStatusSummary(ReportFilter f)throws SQLException{
        String sql="""
            SELECT STATUS,COUNT(*) FROM PATIENTS
            WHERE CREATED_AT>=? AND CREATED_AT<?
            GROUP BY STATUS ORDER BY STATUS
            """;
        List<PatientStatusSummary> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            dates(p,1,f);try(ResultSet r=p.executeQuery()){while(r.next())out.add(new PatientStatusSummary(r.getString(1),r.getLong(2)));}
        }return out;
    }

    @Override
    public List<DoctorDepartmentSummary> getDoctorDepartmentSummary(ReportFilter f)throws SQLException{
        String sql="""
            SELECT dep.DEPARTMENT_ID,dep.DEPARTMENT_NAME,
                   COUNT(DISTINCT d.DOCTOR_ID),
                   COUNT(DISTINCT CASE WHEN d.STATUS='ACTIVE' THEN d.DOCTOR_ID END),
                   COUNT(DISTINCT a.APPOINTMENT_ID)
            FROM DEPARTMENTS dep
            LEFT JOIN DOCTORS d ON d.DEPARTMENT_ID=dep.DEPARTMENT_ID
              AND (? IS NULL OR d.DOCTOR_ID=?)
            LEFT JOIN APPOINTMENTS a ON a.DOCTOR_ID=d.DOCTOR_ID
              AND a.APPOINTMENT_DATE>=? AND a.APPOINTMENT_DATE<?
            WHERE (? IS NULL OR dep.DEPARTMENT_ID=?)
            GROUP BY dep.DEPARTMENT_ID,dep.DEPARTMENT_NAME
            ORDER BY dep.DEPARTMENT_NAME
            """;
        List<DoctorDepartmentSummary> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=nullablePair(p,1,f.getDoctorId());i=dates(p,i,f);nullablePair(p,i,f.getDepartmentId());
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new DoctorDepartmentSummary(r.getLong(1),r.getString(2),r.getLong(3),r.getLong(4),r.getLong(5)));}
        }return out;
    }

    @Override
    public List<AdmissionSummary> getAdmissionStatusSummary(ReportFilter f)throws SQLException{
        String sql="""
            SELECT STATUS,COUNT(*) FROM ADMISSIONS
            WHERE ADMISSION_DATE>=? AND ADMISSION_DATE<?
              AND (? IS NULL OR DEPARTMENT_ID=?) AND (? IS NULL OR DOCTOR_ID=?)
              AND (? IS NULL OR STATUS=?)
            GROUP BY STATUS ORDER BY STATUS
            """;
        List<AdmissionSummary> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=dates(p,1,f);i=nullablePair(p,i,f.getDepartmentId());i=nullablePair(p,i,f.getDoctorId());textPair(p,i,f.getAdmissionStatus());
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new AdmissionSummary(r.getString(1),r.getLong(2)));}
        }return out;
    }

    @Override
    public List<BedOccupancySummary> getBedOccupancySummary(ReportFilter f)throws SQLException{
        String sql="""
            SELECT r.ROOM_TYPE,COUNT(*),
                   SUM(CASE WHEN b.STATUS='AVAILABLE' THEN 1 ELSE 0 END),
                   SUM(CASE WHEN b.STATUS='OCCUPIED' THEN 1 ELSE 0 END),
                   SUM(CASE WHEN b.STATUS='MAINTENANCE' THEN 1 ELSE 0 END),
                   ROUND(100*SUM(CASE WHEN b.STATUS='OCCUPIED' THEN 1 ELSE 0 END)/NULLIF(COUNT(*),0),2)
            FROM BEDS b JOIN ROOMS r ON r.ROOM_ID=b.ROOM_ID
            WHERE b.STATUS<>'INACTIVE' AND r.STATUS<>'INACTIVE'
              AND (? IS NULL OR r.DEPARTMENT_ID=?) AND (? IS NULL OR r.ROOM_TYPE=?)
            GROUP BY r.ROOM_TYPE ORDER BY r.ROOM_TYPE
            """;
        List<BedOccupancySummary> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=nullablePair(p,1,f.getDepartmentId());textPair(p,i,f.getRoomType());
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new BedOccupancySummary(r.getString(1),r.getLong(2),r.getLong(3),r.getLong(4),r.getLong(5),money(r,6)));}
        }return out;
    }

    @Override
    public List<BillingStatusSummary> getBillingStatusSummary(ReportFilter f)throws SQLException{
        String sql="""
            SELECT STATUS,COUNT(*),NVL(SUM(TOTAL_AMOUNT),0),NVL(SUM(PAID_AMOUNT),0),NVL(SUM(BALANCE_AMOUNT),0)
            FROM BILLS WHERE BILL_DATE>=? AND BILL_DATE<? AND (? IS NULL OR STATUS=?)
            GROUP BY STATUS ORDER BY STATUS
            """;
        List<BillingStatusSummary> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=dates(p,1,f);textPair(p,i,f.getBillStatus());
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new BillingStatusSummary(r.getString(1),r.getLong(2),money(r,3),money(r,4),money(r,5)));}
        }return out;
    }

    @Override
    public List<RevenueTrendPoint> getRevenueTrend(ReportFilter f)throws SQLException{
        String sql="""
            SELECT REPORT_DATE,SUM(BILLED),SUM(PAID),SUM(REFUNDED)
            FROM (
              SELECT TRUNC(BILL_DATE) REPORT_DATE,SUM(TOTAL_AMOUNT) BILLED,0 PAID,0 REFUNDED
              FROM BILLS WHERE BILL_DATE>=? AND BILL_DATE<? AND STATUS<>'CANCELLED' GROUP BY TRUNC(BILL_DATE)
              UNION ALL
              SELECT TRUNC(PAYMENT_DATE),0,SUM(AMOUNT),0
              FROM PAYMENTS WHERE PAYMENT_DATE>=? AND PAYMENT_DATE<? AND STATUS='SUCCESS' GROUP BY TRUNC(PAYMENT_DATE)
              UNION ALL
              SELECT TRUNC(PAYMENT_DATE),0,0,SUM(AMOUNT)
              FROM PAYMENTS WHERE PAYMENT_DATE>=? AND PAYMENT_DATE<? AND STATUS='REFUNDED' GROUP BY TRUNC(PAYMENT_DATE)
            ) GROUP BY REPORT_DATE ORDER BY REPORT_DATE
            """;
        List<RevenueTrendPoint> out=new ArrayList<>();
        try(Connection c=connection();PreparedStatement p=c.prepareStatement(sql)){
            int i=dates(p,1,f);i=dates(p,i,f);dates(p,i,f);
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new RevenueTrendPoint(r.getDate(1).toLocalDate(),money(r,2),money(r,3),money(r,4)));}
        }return out;
    }

    protected Connection connection()throws SQLException{return DatabaseConnectionManager.getConnection();}
    private int dates(PreparedStatement p,int i,ReportFilter f)throws SQLException{
        p.setDate(i++,Date.valueOf(f.getDateFrom()));p.setDate(i++,Date.valueOf(f.getDateTo().plusDays(1)));return i;
    }
    private int nullablePair(PreparedStatement p,int i,Long v)throws SQLException{
        if(v==null){p.setNull(i++,Types.NUMERIC);p.setNull(i++,Types.NUMERIC);}else{p.setLong(i++,v);p.setLong(i++,v);}return i;
    }
    private int textPair(PreparedStatement p,int i,String v)throws SQLException{
        if(v==null){p.setNull(i++,Types.VARCHAR);p.setNull(i++,Types.VARCHAR);}else{p.setString(i++,v);p.setString(i++,v);}return i;
    }
    private BigDecimal money(ResultSet r,int column)throws SQLException{BigDecimal v=r.getBigDecimal(column);return v==null?BigDecimal.ZERO.setScale(2):v;}
}
