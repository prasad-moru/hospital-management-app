package com.hospital.management.util;

/** Safe RFC-style CSV field encoding with spreadsheet formula-injection protection. */
public final class CsvUtil {
    private CsvUtil(){}
    public static String escape(Object value){
        String text=value==null?"":String.valueOf(value);
        if(!text.isEmpty()&&"=+-@".indexOf(text.charAt(0))>=0)text="'"+text;
        if(text.indexOf(',')>=0||text.indexOf('"')>=0||text.indexOf('\r')>=0||text.indexOf('\n')>=0)
            return "\""+text.replace("\"","\"\"")+"\"";
        return text;
    }
    public static String row(Object... values){
        StringBuilder out=new StringBuilder();
        for(int i=0;i<values.length;i++){if(i>0)out.append(',');out.append(escape(values[i]));}
        return out.append("\r\n").toString();
    }
}
