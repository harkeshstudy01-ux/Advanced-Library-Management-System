package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Fine {

    private int fineId;
    private int loanId;
    private int memberId;
    private String fineType;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String fineStatus;
    private String description;
    private LocalDateTime createdAt;

    public Fine() {
    }

    public Fine(int fineId,
                int loanId,
                int memberId,
                String fineType,
                BigDecimal amount,
                BigDecimal paidAmount,
                String fineStatus,
                String description,
                LocalDateTime createdAt) {

        this.fineId = fineId;
        this.loanId = loanId;
        this.memberId = memberId;
        this.fineType = fineType;
        this.amount = amount;
        this.paidAmount = paidAmount;
        this.fineStatus = fineStatus;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getFineType() {
        return fineType;
    }

    public void setFineType(String fineType) {
        this.fineType = fineType;
    }

    /*
     * Database column: amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /*
     * Compatibility methods.
     *
     * Existing Main.java/service code may use
     * getFineAmount()/setFineAmount().
     *
     * These methods still use the database-correct
     * 'amount' field.
     */
    public BigDecimal getFineAmount() {
        return amount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.amount = fineAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(String fineStatus) {
        this.fineStatus = fineStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /*
     * Returns the unpaid portion of the fine.
     */
    public BigDecimal getRemainingAmount() {

        if (amount == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal paid = paidAmount == null
                ? BigDecimal.ZERO
                : paidAmount;

        return amount.subtract(paid).max(BigDecimal.ZERO);
    }

    /*
     * A fine is considered paid when nothing remains.
     */
    public boolean isPaid() {
        return getRemainingAmount()
                .compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public String toString() {

        return "Fine{" +
                "fineId=" + fineId +
                ", loanId=" + loanId +
                ", memberId=" + memberId +
                ", fineType='" + fineType + '\'' +
                ", amount=" + amount +
                ", paidAmount=" + paidAmount +
                ", fineStatus='" + fineStatus + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

