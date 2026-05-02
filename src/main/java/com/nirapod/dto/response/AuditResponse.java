package com.nirapod.dto.response;

import com.nirapod.model.Audit;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Audit.
 * taxpayerName and tinNumber are resolved fresh from the Taxpayer FK — never stored.
 */
public class AuditResponse {

    private Long id;
    private Long taxpayerId;
    private String taxpayerName;  // resolved via JOIN, never stored
    private String tinNumber;     // resolved via taxpayer.getTinNumber()
    private Audit entity;

    public static AuditResponse from(Audit e) {
        AuditResponse r = new AuditResponse();
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
    public Audit getAudit() { return entity; }
    public String getStatus() { return entity != null ? entity.getStatus() : null; }
}
