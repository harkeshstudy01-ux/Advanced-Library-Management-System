package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Book {

    private int bookId;
    private String isbn;
    private String title;
    private String subtitle;
    private int categoryId;
    private Integer publisherId;
    private Integer publicationYear;
    private String edition;
    private String language;
    private Integer pages;
    private String description;
    private BigDecimal price;
    private LocalDate addedDate;
    private String bookStatus;

    public Book() {
    }

    public Book(int bookId,
                String isbn,
                String title,
                String subtitle,
                int categoryId,
                Integer publisherId,
                Integer publicationYear,
                String edition,
                String language,
                Integer pages,
                String description,
                BigDecimal price,
                LocalDate addedDate,
                String bookStatus) {

        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.subtitle = subtitle;
        this.categoryId = categoryId;
        this.publisherId = publisherId;
        this.publicationYear = publicationYear;
        this.edition = edition;
        this.language = language;
        this.pages = pages;
        this.description = description;
        this.price = price;
        this.addedDate = addedDate;
        this.bookStatus = bookStatus;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(bookStatus);
    }

    @Override
    public String toString() {

        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", categoryId=" + categoryId +
                ", publisherId=" + publisherId +
                ", publicationYear=" + publicationYear +
                ", edition='" + edition + '\'' +
                ", language='" + language + '\'' +
                ", pages=" + pages +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", addedDate=" + addedDate +
                ", bookStatus='" + bookStatus + '\'' +
                '}';
    }
}