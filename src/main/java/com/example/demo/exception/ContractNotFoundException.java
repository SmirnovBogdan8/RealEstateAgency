package com.example.demo.exception;

public class ContractNotFoundException extends Exception {
    public ContractNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContractNotFoundException(String message) {
        super(message);
    }
}
