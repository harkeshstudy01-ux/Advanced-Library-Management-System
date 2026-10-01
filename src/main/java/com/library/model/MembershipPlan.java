package com.library.model;

import java.math.BigDecimal;

/**
 * Represents a library membership plan.
 *
 * The values are loaded from the membership_plans database table.
 */
public class MembershipPlan {

    private int planId;
    private String planName;
    private int durationMonths;
    private int maxBooks;
    private int loanPeriodDays;
    private BigDecimal finePerDay;
    private BigDecimal membershipFee;

    public MembershipPlan() {
    }

    public MembershipPlan(int planId,
                          String planName,
                          int durationMonths,
                          int maxBooks,
                          int loanPeriodDays,
                          BigDecimal finePerDay,
                          BigDecimal membershipFee) {

        this.planId = planId;
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.maxBooks = maxBooks;
        this.loanPeriodDays = loanPeriodDays;
        this.finePerDay = finePerDay;
        this.membershipFee = membershipFee;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public void setMaxBooks(int maxBooks) {
        this.maxBooks = maxBooks;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public void setLoanPeriodDays(int loanPeriodDays) {
        this.loanPeriodDays = loanPeriodDays;
    }

    public BigDecimal getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(BigDecimal finePerDay) {
        this.finePerDay = finePerDay;
    }

    public BigDecimal getMembershipFee() {
        return membershipFee;
    }

    public void setMembershipFee(BigDecimal membershipFee) {
        this.membershipFee = membershipFee;
    }

    @Override
    public String toString() {
        return "MembershipPlan{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", durationMonths=" + durationMonths +
                ", maxBooks=" + maxBooks +
                ", loanPeriodDays=" + loanPeriodDays +
                ", finePerDay=" + finePerDay +
                ", membershipFee=" + membershipFee +
                '}';
    }
}

