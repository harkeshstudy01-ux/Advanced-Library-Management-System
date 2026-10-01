package com.library.controller;

import com.library.model.Book;
import com.library.model.BookCopy;
import com.library.service.BookService;

import java.util.List;

public class BookController {

    private final BookService bookService;

    public BookController() {
        this.bookService = new BookService();
    }

    public Book getBook(int bookId) {
        return bookService.getBookById(bookId);
    }

    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    public List<Book> searchBooks(String keyword) {
        return bookService.searchBooks(keyword);
    }

    public BookCopy getCopy(int copyId) {
        return bookService.getCopyById(copyId);
    }

    public List<BookCopy> getCopies(int bookId) {
        return bookService.getCopiesForBook(bookId);
    }

    public List<BookCopy> getAvailableCopies(int bookId) {
        return bookService.getAvailableCopies(bookId);
    }
}