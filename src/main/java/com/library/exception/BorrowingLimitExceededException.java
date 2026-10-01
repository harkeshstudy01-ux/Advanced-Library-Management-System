package com.library.exception;

public class BorrowingLimitExceededException extends LibraryException {

    public BorrowingLimitExceededException(String message) {
        super(message);
    }
}