package com.example.demo.exception;

import lombok.Getter;

import java.util.List;

public class RealEstateValidationException extends Exception {
    @Getter
    private List<String> errorList;

    public RealEstateValidationException() {
        super();
    }

    public RealEstateValidationException(List<String> errorList) {
        super();
        this.errorList = errorList;
    }
}
