package com.nirapod.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nirapod.model.BusinessCategory;

@Repository
public interface BusinessCategoryDAO extends  JpaRepository<BusinessCategory, Long>{

}
