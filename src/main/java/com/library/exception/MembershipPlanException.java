package com.library.exception;

public class MembershipPlanException extends LibraryException {

    public MembershipPlanException(String message) {
        super(message);
    }

    public MembershipPlanException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}

