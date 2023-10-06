package com.example.demo.service;

import com.example.demo.exception.*;
import com.example.demo.ftp.FtpClient;
import com.example.demo.model.Contract;
import com.example.demo.model.ContractFindModel;
import com.example.demo.store.AgencyStore;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AgencyServiceImpl implements AgencyService {
    @Autowired
    AgencyStore agencyStore;
    @Autowired
    FtpClient ftpClient;
    @Autowired
    RealEstateService realEstateService;

    @Autowired
    private Validator validator;

    @Override
    public Contract createContract(Contract contract) throws ContractException, ContractValidationException {
        validate(contract);
        Long realEstateId = contract.getRealEstateId();

        try {
            realEstateService.getById(realEstateId);
        } catch (RealEstateNotFoundException e) {
            throw new ContractException("create contract error: real estate with id " + realEstateId + " not found");
        }

        Contract creation = Contract.builder()
                .creationDate(Timestamp.from(Instant.now()))
                .approved(null)
                .internalId(UUID.randomUUID())
                .realEstateId(realEstateId)
                .clientName(contract.getClientName())
                .employeeName(contract.getEmployeeName())
                .build();

        agencyStore.create(creation);

        return creation;
    }

    @Override
    public Contract findByInternalId(UUID id) throws ContractNotFoundException {
        return agencyStore.findByInternalId(id);
    }

    @Override
    public void approve(UUID id) throws ContractException, ContractApproveException, ContractNotFoundException {
        Contract contract = findByInternalId(id);
        if (contract.getApproved() == null) {
            agencyStore.approve(id);
            return;
        }
        if (contract.getApproved()) {
            throw new ContractApproveException("Contract with id " + id + " is already approved");
        } else {
            throw new ContractApproveException("can not approve contract with id " + id + " .Contract was disapproved");
        }
    }

    @Override
    public void disapprove(UUID id) throws ContractException, ContractApproveException, ContractNotFoundException {
        Contract contract = findByInternalId(id);
        if (contract.getApproved() == null) {
            agencyStore.disapprove(id);
            return;
        }
        if (!contract.getApproved()) {
            throw new ContractApproveException("Contract with id " + id + " is already disapproved");
        } else {
            throw new ContractApproveException("can not approve contract with id " + id + " .Contract was approved");
        }
    }

    @Override
    public List<Contract> find(ContractFindModel contractFindModel) throws ContractNotFoundException {
        if (contractFindModel.getContract() == null) {
            return agencyStore.find(
                    contractFindModel.getDateFrom(),
                    contractFindModel.getDateTo(),
                    null,
                    null,
                    null,
                    null
            );
        }
        Long realEstateId = contractFindModel.getContract().getRealEstateId();
        Boolean approved = contractFindModel.getContract().getApproved();
        String clientName = contractFindModel.getContract().getClientName();
        String employeeName = contractFindModel.getContract().getEmployeeName();

        List<Contract> contractList = agencyStore.find(
                contractFindModel.getDateFrom(),
                contractFindModel.getDateTo(),
                realEstateId,
                approved,
                clientName,
                employeeName
        );

        if (contractList.isEmpty()) throw new ContractNotFoundException("No contracts were found");
        return contractList;
    }

    @Scheduled(cron = ("${contracts.export.cron}"))
    public void exportContracts() throws IOException, ContractNotFoundException {
        ContractFindModel contractFindModel = ContractFindModel.builder()
                .dateFrom(Timestamp.from(Instant.now().minusSeconds(1000000)))
                .dateTo(Timestamp.from(Instant.now()))
                .contract(Contract.builder()
                        .approved(true).build())
                .build();
        List<Contract> found = find(contractFindModel);

        if (found == null || found.isEmpty()) {
            log.info("No contracts to export");
            return;
        }


        StringBuilder str = new StringBuilder();
        str.append("internalId,realEstateId,approved,clientName,employeeName,creationDate\n");
        for (Contract contract : found) {
            str.append(contract.toString()).append("\n");
        }


        if (!ftpClient.isConnected()) {
            ftpClient.open();
        }
        boolean success = false;
        String filename = "contracts_" + Instant.now().toString() + ".csv";
        if (ftpClient.isConnected()) {
            success = ftpClient.upload(str.toString(), filename);
        }
        if (!success) {
            log.error("can not write to ftp server");
        } else {
            log.info("successfully uploaded file {}", filename);
        }

        ftpClient.close();
    }

    private void validate(Contract contract) throws ContractValidationException {
        Set<ConstraintViolation<Contract>> constraintViolations = validator.validate(contract);

        List<String> validationErrorList = constraintViolations.stream()
                .map(constraintViolation -> "Validation error: " + constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage())
                .collect(Collectors.toList());

        if (validationErrorList.isEmpty()) return;
        throw new ContractValidationException(validationErrorList);
    }
}
