package com.hospital.management.util;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class CsvUtilTest{
 @Test void normalAndNull(){assertEquals("value",CsvUtil.escape("value"));assertEquals("",CsvUtil.escape(null));}
 @Test void commaQuoteAndNewlineAreQuoted(){assertEquals("\"a,b\"",CsvUtil.escape("a,b"));assertEquals("\"a\"\"b\"",CsvUtil.escape("a\"b"));assertEquals("\"a\nb\"",CsvUtil.escape("a\nb"));}
 @Test void formulaPrefixesAreNeutralized(){for(String x:new String[]{"=1+1","+cmd","-2","@sum"})assertTrue(CsvUtil.escape(x).startsWith("'"));}
}
