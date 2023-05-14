package com.safebox.config.exceptions;

public class SafeBoxNotAuthorizedException extends RuntimeException {

    public SafeBoxNotAuthorizedException() {
        super("Invalid safebox credentials");
    }

    public SafeBoxNotAuthorizedException(String message) {
        super(message);
    }

}
