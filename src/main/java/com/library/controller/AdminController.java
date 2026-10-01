package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;

import java.util.List;

public class AdminController {

    private final BookService bookService;

    public AdminController() {
        this.bookService = new BookService();
    }

    public List<Book> viewAllBooks() {
        return bookService.getAllBooks();
    }

    public List<Book> searchBooks(String keyword) {
        return bookService.searchBooks(keyword);
    }
}