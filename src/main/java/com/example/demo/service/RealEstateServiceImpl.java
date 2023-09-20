package com.example.demo.service;

import com.example.demo.exception.RealEstateException;
import com.example.demo.exception.RealEstateNotFoundException;
import com.example.demo.model.Address;
import com.example.demo.model.RealEstate;
import com.example.demo.store.RealEstateStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealEstateServiceImpl implements RealEstateService {

    @Autowired
    RealEstateStore realEstateStore;

    @Override
    public void create(RealEstate realEstateEntity) throws RealEstateException {
        realEstateStore.create(realEstateEntity);
    }

    @Override
    public void update(RealEstate realEstateEntity) throws RealEstateException, RealEstateNotFoundException {
        Long id = realEstateEntity.getId();
        if (id == null) throw new RealEstateException("real estate id is required");
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
    public List<RealEstate> findByAddress(Address address) throws RealEstateNotFoundException {
        List<RealEstate> realEstates = realEstateStore.getByAddress(address);
        if (realEstates.isEmpty()) {
            throw new RealEstateNotFoundException("real estates with address " + address + " not found");
        }
        return realEstates;
    }
}
