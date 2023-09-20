package com.example.demo.exception;

public class RealEstateNotFoundException extends Exception {
    public RealEstateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RealEstateNotFoundException(String message) {
        super(message);
    }
}
