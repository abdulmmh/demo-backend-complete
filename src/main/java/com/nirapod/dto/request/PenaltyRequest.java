package com.nirapod.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request DTO for Penalty.
 * taxpayerId is the only taxpayer-related field accepted — name and TIN are resolved server-side.
 */
public class PenaltyRequest {

    @NotNull(message = "taxpayerId is required")
    private Long taxpayerId;

    private String penaltyType;
    private String severity;
    private Double penaltyAmount;
    private Double interestAmount;
    private String returnNo;
    private String assessmentYear;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String issuedBy;
    private String description;
    private String remarks;

    public Long getTaxpayerId() { return taxpayerId; }
    public void setTaxpayerId(Long taxpayerId) { this.taxpayerId = taxpayerId; }

    public String getPenaltyType() { return penaltyType; }
    public void setPenaltyType(String penaltyType) { this.penaltyType = penaltyType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public Double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(Double penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public Double getInterestAmount() { return interestAmount; }
    public void setInterestAmount(Double interestAmount) { this.interestAmount = interestAmount; }
    public String getReturnNo() { return returnNo; }
    public void setReturnNo(String returnNo) { this.returnNo = returnNo; }
    public String getAssessmentYear() { return assessmentYear; }
    public void setAssessmentYear(String assessmentYear) { this.assessmentYear = assessmentYear; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
