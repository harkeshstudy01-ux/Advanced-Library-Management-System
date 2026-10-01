package com.library.service;

import com.library.model.Book;
import com.library.model.BookCopy;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    public BookService() {
        this.bookRepository = new BookRepository();
        this.bookCopyRepository = new BookCopyRepository();
    }

    public Book getBookById(int bookId) {

        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be greater than zero.");
        }

        Optional<Book> book = bookRepository.findById(bookId);

        if (book.isEmpty()) {
            throw new IllegalArgumentException(
                    "Book not found with ID: " + bookId
            );
        }

        return book.get();
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty."
            );
        }

        return bookRepository.search(keyword.trim());
    }

    public BookCopy getCopyById(int copyId) {

        if (copyId <= 0) {
            throw new IllegalArgumentException(
                    "Copy ID must be greater than zero."
            );
        }

        Optional<BookCopy> copy = bookCopyRepository.findById(copyId);

        if (copy.isEmpty()) {
            throw new IllegalArgumentException(
                    "Book copy not found with ID: " + copyId
            );
        }

        return copy.get();
    }

    public List<BookCopy> getCopiesForBook(int bookId) {

        getBookById(bookId);

        return bookCopyRepository.findByBookId(bookId);
    }

    public List<BookCopy> getAvailableCopies(int bookId) {

        getBookById(bookId);

        return bookCopyRepository.findAvailableByBookId(bookId);
    }

    public boolean isCopyAvailable(int copyId) {

        BookCopy copy = getCopyById(copyId);

        return copy.isAvailable();
    }

    public void markCopyAvailable(int copyId) {

        getCopyById(copyId);

        boolean updated =
                bookCopyRepository.updateAvailability(
                        copyId,
                        "AVAILABLE"
                );

        if (!updated) {
            throw new IllegalStateException(
                    "Unable to mark book copy as available."
            );
        }
    }

    public void markCopyIssued(int copyId) {

        getCopyById(copyId);

        boolean updated =
                bookCopyRepository.updateAvailability(
                        copyId,
                        "ISSUED"
                );

        if (!updated) {
            throw new IllegalStateException(
                    "Unable to mark book copy as issued."
            );
        }
    }
}