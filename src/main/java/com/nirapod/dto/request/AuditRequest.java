package com.nirapod.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request DTO for Audit.
 * taxpayerId is the only taxpayer-related field accepted — name and TIN are resolved server-side.
 */
public class AuditRequest {

    @NotNull(message = "taxpayerId is required")
    private Long taxpayerId;

    private String auditType;
    private String priority;
    private String assessmentYear;
    private String returnNo;
    private LocalDate scheduledDate;
    private String assignedTo;
    private String supervisedBy;
    private String remarks;

    public Long getTaxpayerId() { return taxpayerId; }
    public void setTaxpayerId(Long taxpayerId) { this.taxpayerId = taxpayerId; }

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
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getSupervisedBy() { return supervisedBy; }
    public void setSupervisedBy(String supervisedBy) { this.supervisedBy = supervisedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
