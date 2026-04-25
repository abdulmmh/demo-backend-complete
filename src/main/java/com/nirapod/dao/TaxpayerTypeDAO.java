package com.nirapod.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nirapod.model.TaxpayerType;

@Repository
public interface TaxpayerTypeDAO extends JpaRepository<TaxpayerType, Long> {

}
