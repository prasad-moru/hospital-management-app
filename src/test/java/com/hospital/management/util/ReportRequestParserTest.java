package com.hospital.management.util;
import org.junit.jupiter.api.Test;import java.time.LocalDate;import static org.junit.jupiter.api.Assertions.*;
class ReportRequestParserTest{
 @Test void validDatesAndIds(){var x=ReportRequestParser.parse("2026-07-01","2026-07-23","2","3","completed",null,null,null);assertTrue(x.isValid());assertEquals(LocalDate.of(2026,7,1),x.getFilter().getDateFrom());assertEquals(2L,x.getFilter().getDepartmentId());assertEquals("COMPLETED",x.getFilter().getAppointmentStatus());}
 @Test void missingDatesUseCurrentMonth(){var x=ReportRequestParser.parse(null,null,null,null,null,null,null,null);assertTrue(x.isValid());assertEquals(LocalDate.now().withDayOfMonth(1),x.getFilter().getDateFrom());assertEquals(LocalDate.now(),x.getFilter().getDateTo());}
 @Test void invalidValuesHaveFieldErrors(){var x=ReportRequestParser.parse("bad","2026-07-01","-1","x",null,null,null,null);assertFalse(x.isValid());assertTrue(x.getErrors().containsKey("dateFrom"));assertTrue(x.getErrors().containsKey("departmentId"));assertTrue(x.getErrors().containsKey("doctorId"));}
 @Test void reversedRangeRejected(){var x=ReportRequestParser.parse("2026-07-20","2026-07-01",null,null,null,null,null,null);assertFalse(x.isValid());assertTrue(x.getErrors().containsKey("dateRange"));}
}
