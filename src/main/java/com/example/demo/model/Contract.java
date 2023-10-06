package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class Contract {
    private UUID internalId;
    @NotNull
    private Long realEstateId;
    private Timestamp creationDate;
    private Boolean approved;
    private String clientName;
    private String employeeName;

    @Override
    public String toString() {
        return internalId +
                "," + realEstateId +
                "," + approved +
                "," + clientName +
                "," + employeeName +
                "," + creationDate;
    }
}


