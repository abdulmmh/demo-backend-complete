package com.nirapod.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nirapod.model.Tin;

@Repository
public interface TinDAO extends JpaRepository<Tin, Long> {

    Tin findByTinNumber(String tinNumber);

   
    Optional<Tin> findByTaxpayerId(Long taxpayerId);
}