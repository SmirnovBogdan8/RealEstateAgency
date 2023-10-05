package com.example.demo.exception;

import lombok.Getter;

import java.util.List;

public class RealEstateValidationException extends Exception {
    @Getter
    private List<String> errorList;

    public RealEstateValidationException() {
        super();
    }

    public RealEstateValidationException(String message, List<String> errorList) {
        super(message);
        this.errorList = errorList;
    }
}
