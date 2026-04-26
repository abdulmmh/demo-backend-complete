package com.nirapod.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nirapod.dao.BusinessDAO;
import com.nirapod.dao.TaxpayerDAO;
import com.nirapod.dao.VatRegistrationDAO;
import com.nirapod.dto.BusinessVatStatusDTO;
import com.nirapod.model.Business;
import com.nirapod.model.Taxpayer;
import com.nirapod.model.VatRegistration;

@Service
public class BusinessService {

    @Autowired
    private BusinessDAO businessDAO;
    
    @Autowired
    private TaxpayerDAO taxpayerDAO;
    
    @Autowired
    private VatRegistrationDAO vatRegistrationDAO;
    
    
    public List<BusinessVatStatusDTO> getByTaxpayerWithVatStatus(Long taxpayerId) {
    	 
        // 1. Fetch all active businesses for this taxpayer
        List<Business> businesses = businessDAO.getByTaxpayerId(taxpayerId);
     
        // 2. For each business, check if a VAT registration exists
        //    then wrap both into the DTO — constructor handles null vat gracefully
        return businesses.stream()
            .map(b -> {
                VatRegistration vat = vatRegistrationDAO
                    .findByBusiness_IdAndIsDeletedFalse(b.getId())
                    .orElse(null);
                return new BusinessVatStatusDTO(b, vat);
            })
            .collect(Collectors.toList());
    }
     

    public Business create(Business business) {
    	Taxpayer taxpayer = taxpayerDAO.getById(business.getTaxpayer().getId());
        if (taxpayer == null) {
            throw new IllegalArgumentException("Taxpayer not found.");
        }

        // Block company taxpayers
        String typeName = taxpayer.getTaxpayerType() != null
            ? taxpayer.getTaxpayerType().getTypeName().toLowerCase() : "";
        if (typeName.contains("company")) {
            throw new IllegalStateException(
                "Company taxpayers cannot have a separate Business Registration. " +
                "Proceed to VAT Registration directly."
            );
        }

        return businessDAO.save(business);
    }

    public List<Business> getAll() {
        return businessDAO.getAll();
    }

    public Business getById(Long id) {
        Business b = businessDAO.getById(id);
        if (b == null || b.isDeleted()) {
            throw new IllegalArgumentException("Business not found with ID: " + id);
        }
        return b;
    }
    public Business update(Business business) {
        return businessDAO.update(business);
    }


    public void softDelete(Long id) {
        Business business = getById(id);  
        business.setDeleted(true);
        businessDAO.update(business);
    }
}