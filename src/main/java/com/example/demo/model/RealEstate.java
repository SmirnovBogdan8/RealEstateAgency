package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
public class RealEstate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Address address;
    private Integer numberOfRooms;
    private Double area;
    private RealEstateType type;
    private Double price;
    private Integer commission;
}
