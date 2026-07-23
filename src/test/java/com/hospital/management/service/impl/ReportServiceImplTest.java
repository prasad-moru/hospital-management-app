package com.hospital.management.service.impl;
import com.hospital.management.dao.ReportDao;import com.hospital.management.model.report.*;import org.junit.jupiter.api.Test;import java.sql.SQLException;import java.time.LocalDate;import java.util.*;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
class ReportServiceImplTest{
 private ReportFilter filter(){return new ReportFilter(LocalDate.now().minusDays(1),LocalDate.now(),null,null,null,null,null,null);}
 @Test void successfulOverview()throws Exception{ReportDao d=mock(ReportDao.class);HospitalOverviewReport expected=HospitalOverviewReport.empty();when(d.getHospitalOverview(any())).thenReturn(expected);assertSame(expected,new ReportServiceImpl(d).getHospitalOverview(filter()));}
 @Test void nullListBecomesEmpty()throws Exception{ReportDao d=mock(ReportDao.class);when(d.getAppointmentTrend(any())).thenReturn(null);assertTrue(new ReportServiceImpl(d).getAppointmentTrend(filter()).isEmpty());}
 @Test void sqlFailureIsSafe()throws Exception{ReportDao d=mock(ReportDao.class);when(d.getBillingStatusSummary(any())).thenThrow(new SQLException("private detail"));assertTrue(new ReportServiceImpl(d).getBillingStatusSummary(filter()).isEmpty());}
 @Test void invalidRangeDoesNotCallDao(){ReportDao d=mock(ReportDao.class);ReportFilter bad=new ReportFilter(LocalDate.now(),LocalDate.now().minusDays(1),null,null,null,null,null,null);assertTrue(new ReportServiceImpl(d).getRevenueTrend(bad).isEmpty());verifyNoInteractions(d);}
}
