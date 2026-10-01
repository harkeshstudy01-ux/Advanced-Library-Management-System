package com.library.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Loan {

    private int loanId;
    private int copyId;
    private int memberId;
    private Integer librarianId;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String loanStatus;
    private Integer renewalCount;

    public Loan() {
    }

    public Loan(int loanId,
                int copyId,
                int memberId,
                Integer librarianId,
                LocalDateTime issueDate,
                LocalDateTime dueDate,
                LocalDateTime returnDate,
                String loanStatus,
                Integer renewalCount) {

        this.loanId = loanId;
        this.copyId = copyId;
        this.memberId = memberId;
        this.librarianId = librarianId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.loanStatus = loanStatus;
        this.renewalCount = renewalCount;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getCopyId() {
        return copyId;
    }

    public void setCopyId(int copyId) {
        this.copyId = copyId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public Integer getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(Integer librarianId) {
        this.librarianId = librarianId;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    public boolean isReturned() {
        return "RETURNED".equalsIgnoreCase(loanStatus);
    }

    public boolean isOverdue() {

        if (isReturned()) {
            return false;
        }

        if (dueDate == null) {
            return false;
        }

        return dueDate.isBefore(LocalDateTime.now());
    }

    public long getOverdueDays() {

        if (!isOverdue()) {
            return 0;
        }

        return ChronoUnit.DAYS.between(
                dueDate.toLocalDate(),
                LocalDateTime.now().toLocalDate()
        );
    }

    @Override
    public String toString() {

        return "Loan{" +
                "loanId=" + loanId +
                ", copyId=" + copyId +
                ", memberId=" + memberId +
                ", librarianId=" + librarianId +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", loanStatus='" + loanStatus + '\'' +
                ", renewalCount=" + renewalCount +
                '}';
    }
}