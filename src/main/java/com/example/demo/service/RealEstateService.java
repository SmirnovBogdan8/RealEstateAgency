package com.example.demo.service;


import com.example.demo.exception.RealEstateException;
import com.example.demo.exception.RealEstateNotFoundException;
import com.example.demo.exception.RealEstateValidationException;
import com.example.demo.model.Address;
import com.example.demo.model.RealEstate;

import java.util.List;

public interface RealEstateService {
    void create(RealEstate realEstateEntity) throws RealEstateException, RealEstateValidationException;

    void update(RealEstate realEstateEntity) throws RealEstateException, RealEstateNotFoundException, RealEstateValidationException;

    void delete(Long id) throws RealEstateException, RealEstateNotFoundException;

    List<RealEstate> getAll() throws RealEstateNotFoundException;

    RealEstate getById(Long id) throws RealEstateNotFoundException;

    List<RealEstate> findByAddress(Address address) throws RealEstateNotFoundException, RealEstateValidationException;

}
