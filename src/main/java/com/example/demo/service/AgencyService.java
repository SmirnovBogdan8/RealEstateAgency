package com.example.demo.service;

import com.example.demo.exception.ContractApproveException;
import com.example.demo.exception.ContractException;
import com.example.demo.model.Contract;
import com.example.demo.model.ContractFindModel;

import java.util.List;
import java.util.UUID;

public interface AgencyService {
    Contract createContract(Contract contract) throws ContractException;

    Contract findByInternalId(UUID id) throws ContractException;

    void approve(UUID id) throws ContractException, ContractApproveException;

    void disapprove(UUID id) throws ContractException, ContractApproveException;

    List<Contract> find(ContractFindModel contractFindModel);
}
