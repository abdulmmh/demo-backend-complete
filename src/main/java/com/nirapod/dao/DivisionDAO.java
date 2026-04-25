package com.nirapod.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nirapod.model.Division;

@Repository
public interface DivisionDAO extends JpaRepository<Division, Long> {

}
