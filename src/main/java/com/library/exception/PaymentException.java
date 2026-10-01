package com.library.exception;

/**
 * Exception for payment-related business errors.
 */
public class PaymentException extends LibraryException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
