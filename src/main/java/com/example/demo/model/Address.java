package com.example.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Size;

@Data
@Builder
@ToString
public class Address {
    @Size(max = 32)
    private String region;
    @Size(max = 32)
    private String city;
    @Size(max = 32)
    private String street;
    @Size(max = 5)
    private String houseNumber;
    @Size(max = 5)
    private String apartmentNumber;
    private Integer floor;
}
