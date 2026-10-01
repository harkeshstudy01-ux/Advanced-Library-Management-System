package com.library.model;

import java.time.LocalDateTime;

public class Reservation {

    private int reservationId;
    private int bookId;
    private int memberId;
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate;
    private String reservationStatus;
    private Integer priorityNumber;

    public Reservation() {
    }

    public Reservation(int reservationId,
                       int bookId,
                       int memberId,
                       LocalDateTime reservationDate,
                       LocalDateTime expiryDate,
                       String reservationStatus,
                       Integer priorityNumber) {

        this.reservationId = reservationId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.reservationStatus = reservationStatus;
        this.priorityNumber = priorityNumber;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Integer getPriorityNumber() {
        return priorityNumber;
    }

    public void setPriorityNumber(Integer priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(reservationStatus);
    }

    public boolean isExpired() {
        return expiryDate != null
                && expiryDate.isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", bookId=" + bookId +
                ", memberId=" + memberId +
                ", reservationDate=" + reservationDate +
                ", expiryDate=" + expiryDate +
                ", reservationStatus='" + reservationStatus + '\'' +
                ", priorityNumber=" + priorityNumber +
                '}';
    }
}

