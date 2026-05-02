package com.nirapod.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "audit")
@Table(name = "audits")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String auditNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    // ── REMOVED: tinNumber, taxpayerName — read via taxpayer FK ──

    private String auditType;
    private String priority;
    private String assessmentYear;
    private String returnNo;
    private LocalDate scheduledDate;
    private LocalDate startDate;
    private LocalDate completionDate;
    private String assignedTo;
    private String supervisedBy;

    @Column(length = 5000)
    private String auditFindings;

    private Double taxDemand;
    private Double penaltyRecommended;
    private String status;

    @Column(length = 2000)
    private String remarks;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "Scheduled";
        if (this.auditNo == null || this.auditNo.isEmpty())
            this.auditNo = "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAuditNo() { return auditNo; }
    public void setAuditNo(String auditNo) { this.auditNo = auditNo; }
    public Taxpayer getTaxpayer() { return taxpayer; }
    public void setTaxpayer(Taxpayer taxpayer) { this.taxpayer = taxpayer; }
    public String getAuditType() { return auditType; }
    public void setAuditType(String auditType) { this.auditType = auditType; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getAssessmentYear() { return assessmentYear; }
    public void setAssessmentYear(String assessmentYear) { this.assessmentYear = assessmentYear; }
    public String getReturnNo() { return returnNo; }
    public void setReturnNo(String returnNo) { this.returnNo = returnNo; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getSupervisedBy() { return supervisedBy; }
    public void setSupervisedBy(String supervisedBy) { this.supervisedBy = supervisedBy; }
    public String getAuditFindings() { return auditFindings; }
    public void setAuditFindings(String auditFindings) { this.auditFindings = auditFindings; }
    public Double getTaxDemand() { return taxDemand; }
    public void setTaxDemand(Double taxDemand) { this.taxDemand = taxDemand; }
    public Double getPenaltyRecommended() { return penaltyRecommended; }
    public void setPenaltyRecommended(Double penaltyRecommended) { this.penaltyRecommended = penaltyRecommended; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
