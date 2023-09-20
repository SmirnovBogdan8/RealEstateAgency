package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ContractFindModel {
    private Timestamp dateFrom;
    private Timestamp dateTo;

    @JsonProperty("filter")
    private Contract contract;
}
