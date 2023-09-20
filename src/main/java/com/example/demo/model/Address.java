package com.example.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Address {
    private String region;
    private String city;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
    private Integer floor;
}
