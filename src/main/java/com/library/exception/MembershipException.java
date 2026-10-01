package com.library.exception;

/**
 * Exception for membership and membership-plan related
 * business errors.
 */
public class MembershipException extends LibraryException {

    public MembershipException(String message) {
        super(message);
    }

    public MembershipException(String message, Throwable cause) {
        super(message, cause);
    }
}

