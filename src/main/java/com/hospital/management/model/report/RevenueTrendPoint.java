package com.hospital.management.model.report;
import java.math.BigDecimal;import java.time.LocalDate;
/** Immutable daily billed, successful-payment and refunded-payment totals. */
public final class RevenueTrendPoint {
    private final LocalDate reportDate; private final BigDecimal billedAmount,paidAmount,refundedAmount;
    public RevenueTrendPoint(LocalDate reportDate,BigDecimal billedAmount,BigDecimal paidAmount,BigDecimal refundedAmount){
        this.reportDate=reportDate;this.billedAmount=money(billedAmount);this.paidAmount=money(paidAmount);this.refundedAmount=money(refundedAmount);
    }
    private static BigDecimal money(BigDecimal v){return v==null?BigDecimal.ZERO.setScale(2):v.setScale(2);}
    public LocalDate getReportDate(){return reportDate;} public BigDecimal getBilledAmount(){return billedAmount;}
    public BigDecimal getPaidAmount(){return paidAmount;} public BigDecimal getRefundedAmount(){return refundedAmount;}
}
