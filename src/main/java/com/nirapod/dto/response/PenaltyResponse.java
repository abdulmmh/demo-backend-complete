package com.nirapod.dto.response;

import com.nirapod.model.Penalty;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Penalty.
 * taxpayerName and tinNumber are resolved fresh from the Taxpayer FK — never stored.
 */
public class PenaltyResponse {

    private Long id;
    private Long taxpayerId;
    private String taxpayerName;  // resolved via JOIN, never stored
    private String tinNumber;     // resolved via taxpayer.getTinNumber()
    private Penalty entity;

    public static PenaltyResponse from(Penalty e) {
        PenaltyResponse r = new PenaltyResponse();
        Number rawId = (Number) e.getId();
        r.id = rawId.longValue();
        if (e.getTaxpayer() != null) {
            r.taxpayerId   = e.getTaxpayer().getId();
            r.taxpayerName = e.getTaxpayer().getFullName() != null
                ? e.getTaxpayer().getFullName()
                : e.getTaxpayer().getCompanyName();
            r.tinNumber    = e.getTaxpayer().getTinNumber();
        }
        r.entity = e;
        return r;
    }

    public Long getId() { return id; }
    public Long getTaxpayerId() { return taxpayerId; }
    public String getTaxpayerName() { return taxpayerName; }
    public String getTinNumber() { return tinNumber; }
    public Penalty getPenalty() { return entity; }
    public String getStatus() { return entity != null ? entity.getStatus() : null; }
}
