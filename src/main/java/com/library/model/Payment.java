
package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private int paymentId;
    private int fineId;
    private int memberId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String paymentStatus;

    public Payment() {
    }

    public Payment(int paymentId,
                   int fineId,
                   int memberId,
                   BigDecimal amount,
                   String paymentMethod,
                   String transactionReference,
                   LocalDateTime paymentDate,
                   String paymentStatus) {

        this.paymentId = paymentId;
        this.fineId = fineId;
        this.memberId = memberId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", fineId=" + fineId +
                ", memberId=" + memberId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionReference='" +
                transactionReference + '\'' +
                ", paymentDate=" + paymentDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}

