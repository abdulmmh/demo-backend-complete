package com.nirapod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nirapod.dao.BusinessCategoryDAO;
import com.nirapod.dao.BusinessTypeDAO;
import com.nirapod.dao.DistrictDAO;
import com.nirapod.dao.DivisionDAO;
import com.nirapod.dao.TaxpayerTypeDAO;
import com.nirapod.dao.TaxZoneDAO;
import com.nirapod.dao.TaxCircleDAO;
import com.nirapod.model.BusinessCategory;
import com.nirapod.model.BusinessType;
import com.nirapod.model.District;
import com.nirapod.model.Division;
import com.nirapod.model.TaxpayerType;
import com.nirapod.model.TaxZone;
import com.nirapod.model.TaxCircle;


@RestController
@RequestMapping("/api/master")
public class MasterDataController {
	
	@Autowired
	private DivisionDAO divisionDAO;
	
	@Autowired
	private DistrictDAO districtDAO;
	
	@Autowired
	private TaxpayerTypeDAO taxpayerTypeDAO;
	
	@Autowired
	private BusinessTypeDAO businessTypeDAO;

	@Autowired
	private BusinessCategoryDAO businessCategoryDAO;

	@Autowired
	private TaxZoneDAO taxZoneDAO;

	@Autowired
	private TaxCircleDAO taxCircleDAO;
	
	@GetMapping("/divisions")
	public List<Division> getDivision() {
		
		return divisionDAO.findAll();
	}
	
	@GetMapping("/divisions/{id}/districts")
	public List<District> getDistrictsByDivision(@PathVariable Long id) {
	    return districtDAO.findByDivisionId(id);
	}
	
	@GetMapping("/districts")
	public List<District> getDistrict() {
		
		return districtDAO.findAll();
	}
	
	@GetMapping("/taxpayer-types")
	public List<TaxpayerType> getTaxpayerType() {
		
		return taxpayerTypeDAO.findAll();
	}
	
	@GetMapping("/business-types")
	public List<BusinessType> getBusinessTypes() {
	    return businessTypeDAO.findAll();
	}
	
	@GetMapping("/business-categories")
	public List<BusinessCategory> getBusinessCategories() {
	    return businessCategoryDAO.findAll();
	}

	// Tax Zones — filtered by district id
	@GetMapping("/districts/{districtId}/tax-zones")
	public List<TaxZone> getTaxZonesByDistrict(@PathVariable Long districtId) {
	    return taxZoneDAO.findByDistrictId(districtId);
	}

	// Tax Circles — filtered by zone id
	@GetMapping("/tax-zones/{zoneId}/tax-circles")
	public List<TaxCircle> getTaxCirclesByZone(@PathVariable Long zoneId) {
	    return taxCircleDAO.findByTaxZoneId(zoneId);
	}
}