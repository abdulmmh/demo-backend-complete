package com.nirapod.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "penalty")
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String penaltyNo;

    private String tinNumber;
    private String taxpayerName;
    private String penaltyType;
    private String severity;
    private Double penaltyAmount;
    private Double interestAmount;
    private Double totalAmount;
    private Double paidAmount;
    private String returnNo;
    private String assessmentYear;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private String status;
    private String issuedBy;
    private String approvedBy;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String remarks;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "Issued";
        if (this.penaltyNo == null || this.penaltyNo.isEmpty())
            this.penaltyNo = "PEN-" + System.currentTimeMillis();
        if (this.penaltyAmount != null && this.interestAmount != null)
            this.totalAmount = this.penaltyAmount + this.interestAmount;
        if (this.paidAmount == null) this.paidAmount = 0.0;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPenaltyNo() { return penaltyNo; }
    public void setPenaltyNo(String penaltyNo) { this.penaltyNo = penaltyNo; }
    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }
    public String getTaxpayerName() { return taxpayerName; }
    public void setTaxpayerName(String taxpayerName) { this.taxpayerName = taxpayerName; }
    public String getPenaltyType() { return penaltyType; }
    public void setPenaltyType(String penaltyType) { this.penaltyType = penaltyType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public Double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(Double penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public Double getInterestAmount() { return interestAmount; }
    public void setInterestAmount(Double interestAmount) { this.interestAmount = interestAmount; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }
    public String getReturnNo() { return returnNo; }
    public void setReturnNo(String returnNo) { this.returnNo = returnNo; }
    public String getAssessmentYear() { return assessmentYear; }
    public void setAssessmentYear(String assessmentYear) { this.assessmentYear = assessmentYear; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
