package com.example.demo.service;

import com.example.demo.exception.RealEstateException;
import com.example.demo.exception.RealEstateNotFoundException;
import com.example.demo.exception.RealEstateValidationException;
import com.example.demo.model.Address;
import com.example.demo.model.RealEstate;
import com.example.demo.store.RealEstateStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RealEstateServiceImpl implements RealEstateService {

    @Autowired
    RealEstateStore realEstateStore;
    @Autowired
    Validator validator;

    @Override
    public void create(RealEstate realEstateEntity) throws RealEstateException, RealEstateValidationException {
        validate(realEstateEntity);
        realEstateStore.create(realEstateEntity);
    }


    @Override
    public void update(RealEstate realEstateEntity) throws RealEstateException, RealEstateNotFoundException, RealEstateValidationException {
        Long id = realEstateEntity.getId();
        if (id == null) throw new RealEstateException("real estate id is required");
        validate(realEstateEntity);
        realEstateStore.update(id, realEstateEntity);
    }

    @Override
    public void delete(Long id) throws RealEstateException, RealEstateNotFoundException {
        realEstateStore.delete(id);
    }

    @Override
    public List<RealEstate> getAll() throws RealEstateNotFoundException {
        List<RealEstate> all = realEstateStore.getAll();
        if (all.isEmpty()) {
            throw new RealEstateNotFoundException("no real estate was found");
        }

        return all;
    }

    @Override
    public RealEstate getById(Long id) throws RealEstateNotFoundException {
        RealEstate realEstate = realEstateStore.getById(id);
        if (realEstate == null) {
            throw new RealEstateNotFoundException("real estate with id " + id + " not found");
        }
        return realEstate;
    }

    @Override
    public List<RealEstate> findByAddress(Address address) throws RealEstateNotFoundException, RealEstateValidationException {
        validateAddress(address);
        List<RealEstate> realEstates = realEstateStore.getByAddress(address);
        if (realEstates.isEmpty()) {
            throw new RealEstateNotFoundException("real estates with address " + address + " not found");
        }
        return realEstates;
    }


    private void validate(RealEstate realEstateEntity) throws RealEstateValidationException {
        Set<ConstraintViolation<RealEstate>> constraintViolations = validator.validate(realEstateEntity);

        List<String> validationErrorList = constraintViolations.stream()
                .map(constraintViolation -> "Validation error: " + constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage())
                .collect(Collectors.toList());

        Set<ConstraintViolation<RealEstate>> commissionValidation = constraintViolations.stream()
                .filter(constraintViolation ->
                        constraintViolation.getPropertyPath().toString().equals("price") ||
                                constraintViolation.getPropertyPath().toString().equals("commission"))
                .collect(Collectors.toSet());

        if (commissionValidation.isEmpty() && realEstateEntity.getCommission() > realEstateEntity.getPrice()) {
            validationErrorList.add("Validation error: commission must be less or equals price");
        }
        if (validationErrorList.isEmpty()) return;
        throw new RealEstateValidationException("", validationErrorList);
    }

    private void validateAddress(Address address) throws RealEstateValidationException {
        Set<ConstraintViolation<Address>> constraintViolations = validator.validate(address);

        List<String> validationErrorList = constraintViolations.stream()
                .map(constraintViolation -> "Validation error: " + constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage())
                .collect(Collectors.toList());

        if (validationErrorList.isEmpty()) return;
        throw new RealEstateValidationException("", validationErrorList);
    }
}
