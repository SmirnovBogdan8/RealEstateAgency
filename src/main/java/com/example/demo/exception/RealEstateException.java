package com.example.demo.exception;

public class RealEstateException extends Exception {
    public RealEstateException() {
        super();
    }

    public RealEstateException(String message) {
        super(message);
    }

    public RealEstateException(String message, Throwable cause) {
        super(message, cause);
    }
}
