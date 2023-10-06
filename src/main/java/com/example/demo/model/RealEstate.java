package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class RealEstate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Valid
    private Address address;
    @NotNull
    @Positive
    private Integer numberOfRooms;
    @NotNull
    @Positive
    private Double area;
    @NotNull
    private RealEstateType type;
    @NotNull
    private Double price;
    @NotNull
    private Integer commission;
}
