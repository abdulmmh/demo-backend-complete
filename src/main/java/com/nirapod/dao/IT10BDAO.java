package com.nirapod.dao;

import com.nirapod.model.IT10B;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IT10BDAO extends JpaRepository<IT10B, Long> {

    Optional<IT10B> findByIncomeTaxReturn_IdAndIsDeletedFalse(Long returnId);

    boolean existsByIncomeTaxReturn_IdAndIsDeletedFalse(Long returnId);

    Optional<IT10B> findByIdAndIsDeletedFalse(Long id);
}
