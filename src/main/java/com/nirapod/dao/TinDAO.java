package com.nirapod.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.nirapod.model.Tin;

@Repository
public interface TinDAO extends JpaRepository<Tin, Long> {

    Tin findByTinNumber(String tinNumber);

    // taxpayer_id is now a proper FK column in the tins table
    Optional<Tin> findByTaxpayer_Id(Long taxpayerId);

    // Fetch with taxpayer eagerly to avoid N+1 in list queries
    @Query("SELECT t FROM Tin t JOIN FETCH t.taxpayer")
    java.util.List<Tin> findAllWithTaxpayer();
}
