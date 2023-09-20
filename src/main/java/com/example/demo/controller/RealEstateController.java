package com.example.demo.controller;

import com.example.demo.exception.RealEstateNotFoundException;
import com.example.demo.model.Address;
import com.example.demo.model.RealEstate;
import com.example.demo.model.RealEstateAgencyResponse;
import com.example.demo.service.RealEstateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Log4j2
@RequestMapping(path = "/real-estate-service")
public class RealEstateController {

    @Autowired
    RealEstateService realEstateService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RealEstateAgencyResponse> getAll() {
        try {
            List<RealEstate> all = realEstateService.getAll();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response(all)
                            .build());
        } catch (RealEstateNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(RealEstateAgencyResponse
                            .builder()
                            .errors(List.of(e.getMessage()))
                            .build());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RealEstateAgencyResponse> getById(@PathVariable(required = false) Optional<Long> id) {

        if (id.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RealEstateAgencyResponse
                            .builder()
                            .errors(List.of("parameter id is required"))
                            .build());
        }

        try {
            RealEstate realEstate = realEstateService.getById(id.get());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response(realEstate)
                            .build());
        } catch (RealEstateNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(RealEstateAgencyResponse
                            .builder()
                            .errors(List.of(e.getMessage()))
                            .build());
        }
    }


    @GetMapping(value = "/address/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RealEstateAgencyResponse> findByAddress(@RequestBody Address address) {

        try {
            List<RealEstate> realEstates = realEstateService.findByAddress(address);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response(realEstates)
                            .build());
        } catch (RealEstateNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(RealEstateAgencyResponse
                            .builder()
                            .errors(List.of(e.getMessage()))
                            .build());
        }
    }


    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RealEstateAgencyResponse> create(@RequestBody RealEstate realEstate) {
        try {
            realEstateService.create(realEstate);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RealEstateAgencyResponse.builder()
                            .response("OK")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RealEstateAgencyResponse
                            .builder()
                            .errors(List.of(e.getMessage()))
                            .build());
        }
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RealEstateAgencyResponse> delete(@PathVariable Long id) {
        try {
            realEstateService.delete(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response("OK")
                            .build());
        } catch (Exception e) {
            if (e instanceof RealEstateNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(RealEstateAgencyResponse
                                .builder()
                                .errors(List.of(e.getMessage()))
                                .build());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RealEstateAgencyResponse
                            .builder()
                            .errors(List.of(e.getMessage()))
                            .build());
        }
    }

    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RealEstateAgencyResponse> update(@RequestBody RealEstate realEstate) {
        try {
            realEstateService.update(realEstate);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response("OK")
                            .build());
        } catch (Exception e) {
            if (e instanceof RealEstateNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(RealEstateAgencyResponse
                                .builder()
                                .errors(List.of(e.getMessage()))
                                .build());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RealEstateAgencyResponse
                            .builder()
                            .errors(List.of(e.getMessage()))
                            .build());
        }
    }

}
