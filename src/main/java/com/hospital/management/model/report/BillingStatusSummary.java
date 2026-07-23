package com.hospital.management.model.report;
import java.math.BigDecimal;
/** Immutable bill totals grouped by status. */
public final class BillingStatusSummary {
    private final String status; private final long billCount;
    private final BigDecimal totalAmount,paidAmount,balanceAmount;
    public BillingStatusSummary(String status,long billCount,BigDecimal totalAmount,BigDecimal paidAmount,BigDecimal balanceAmount){
        this.status=status;this.billCount=billCount;this.totalAmount=money(totalAmount);this.paidAmount=money(paidAmount);this.balanceAmount=money(balanceAmount);
    }
    private static BigDecimal money(BigDecimal v){return v==null?BigDecimal.ZERO.setScale(2):v.setScale(2);}
    public String getStatus(){return status;} public long getBillCount(){return billCount;}
    public BigDecimal getTotalAmount(){return totalAmount;} public BigDecimal getPaidAmount(){return paidAmount;}
    public BigDecimal getBalanceAmount(){return balanceAmount;}
}
