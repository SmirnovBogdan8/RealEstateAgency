package com.example.demo.exception;

public class ContractException extends Exception {
    public ContractException() {
        super();
    }

    public ContractException(String message) {
        super(message);
    }

    public ContractException(String message, Throwable cause) {
        super(message, cause);
    }
}
