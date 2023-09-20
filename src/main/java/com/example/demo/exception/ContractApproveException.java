package com.example.demo.exception;

public class ContractApproveException extends Exception {
    public ContractApproveException() {
        super();
    }

    public ContractApproveException(String message) {
        super(message);
    }

    public ContractApproveException(String message, Throwable cause) {
        super(message, cause);
    }
}
