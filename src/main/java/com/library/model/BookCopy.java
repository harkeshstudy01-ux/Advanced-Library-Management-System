package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookCopy {

    private int copyId;
    private int bookId;
    private Integer shelfId;
    private String accessionNumber;
    private String barcode;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String conditionStatus;
    private String availabilityStatus;

    public BookCopy() {
    }

    public BookCopy(int copyId,
                    int bookId,
                    Integer shelfId,
                    String accessionNumber,
                    String barcode,
                    LocalDate purchaseDate,
                    BigDecimal purchasePrice,
                    String conditionStatus,
                    String availabilityStatus) {

        this.copyId = copyId;
        this.bookId = bookId;
        this.shelfId = shelfId;
        this.accessionNumber = accessionNumber;
        this.barcode = barcode;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.conditionStatus = conditionStatus;
        this.availabilityStatus = availabilityStatus;
    }

    public int getCopyId() {
        return copyId;
    }

    public void setCopyId(int copyId) {
        this.copyId = copyId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Integer getShelfId() {
        return shelfId;
    }

    public void setShelfId(Integer shelfId) {
        this.shelfId = shelfId;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(availabilityStatus);
    }

    @Override
    public String toString() {

        return "BookCopy{" +
                "copyId=" + copyId +
                ", bookId=" + bookId +
                ", shelfId=" + shelfId +
                ", accessionNumber='" + accessionNumber + '\'' +
                ", barcode='" + barcode + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", purchasePrice=" + purchasePrice +
                ", conditionStatus='" + conditionStatus + '\'' +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                '}';
    }
}
