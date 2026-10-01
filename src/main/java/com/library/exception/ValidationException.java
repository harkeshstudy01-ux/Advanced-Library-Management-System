package com.library.exception;

/**
 * Exception for invalid user input or business validation failures.
 */
public class ValidationException extends LibraryException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
