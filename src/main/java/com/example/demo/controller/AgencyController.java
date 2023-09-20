package com.example.demo.controller;

import com.example.demo.exception.ContractApproveException;
import com.example.demo.model.Contract;
import com.example.demo.model.ContractFindModel;
import com.example.demo.model.RealEstateAgencyResponse;
import com.example.demo.service.AgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/agency-service", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgencyController {

    @Autowired
    AgencyService agencyService;

    @PutMapping("/")
    public ResponseEntity<RealEstateAgencyResponse> createContract(@RequestBody Contract contract) {
        try {
            Contract created = agencyService.createContract(contract);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RealEstateAgencyResponse.builder()
                            .response(created).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RealEstateAgencyResponse.builder()
                            .errors(List.of(e.getMessage()))
                            .build()
                    );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RealEstateAgencyResponse> findById(@PathVariable UUID id) {
        try {
            Contract contract = agencyService.findByInternalId(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response(contract).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RealEstateAgencyResponse.builder()
                            .errors(List.of(e.getMessage()))
                            .build()
                    );
        }
    }

    @GetMapping("/find")
    public ResponseEntity<RealEstateAgencyResponse> find(@RequestBody ContractFindModel contractFindModel) {
        try {
            List<Contract> contracts = agencyService.find(contractFindModel);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response(contracts).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(RealEstateAgencyResponse.builder()
                            .errors(List.of(e.getMessage()))
                            .build()
                    );
        }
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<RealEstateAgencyResponse> approve(@PathVariable UUID id) {
        try {
            agencyService.approve(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response("OK").build());
        } catch (Exception e) {
            if (e instanceof ContractApproveException) {
                return ResponseEntity.badRequest()
                        .body(RealEstateAgencyResponse.builder()
                                .errors(List.of(e.getMessage()))
                                .build()
                        );
            }
            return ResponseEntity.internalServerError()
                    .body(RealEstateAgencyResponse.builder()
                            .errors(List.of(e.getMessage()))
                            .build()
                    );
        }
    }

    @PostMapping("/disapprove/{id}")
    public ResponseEntity<RealEstateAgencyResponse> disapprove(@PathVariable UUID id) {
        try {
            agencyService.disapprove(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(RealEstateAgencyResponse.builder()
                            .response("OK").build());
        } catch (Exception e) {
            if (e instanceof ContractApproveException) {
                return ResponseEntity.badRequest()
                        .body(RealEstateAgencyResponse.builder()
                                .errors(List.of(e.getMessage()))
                                .build()
                        );
            }
            return ResponseEntity.internalServerError()
                    .body(RealEstateAgencyResponse.builder()
                            .errors(List.of(e.getMessage()))
                            .build()
                    );
        }
    }
}
