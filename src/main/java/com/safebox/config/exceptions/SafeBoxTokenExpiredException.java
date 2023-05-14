package com.safebox.config.exceptions;

public class SafeBoxTokenExpiredException extends RuntimeException {
    public SafeBoxTokenExpiredException(String message) {
        super(message);
    }
}
