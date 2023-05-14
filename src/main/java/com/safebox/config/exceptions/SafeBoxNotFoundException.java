package com.safebox.config.exceptions;

public class SafeBoxNotFoundException extends RuntimeException {
    public SafeBoxNotFoundException(String id) {
        super("Safe box with id " + id + " not found");
    }
}
