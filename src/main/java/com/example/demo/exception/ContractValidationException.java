package com.example.demo.exception;

import lombok.Getter;

import java.util.List;

public class ContractValidationException extends Exception {

@Getter
    private List<String> errorList;

    public ContractValidationException(List<String> errorList) {
        super();
        this.errorList = errorList;
    }
}
