package com.nirapod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name = "vatReturns")
@Table(name = "vat_returns")
public class VatReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Return Number — auto-generated in Service ──────────────────────────
    @Column(name = "return_no", nullable = false, unique = true, length = 25)
    private String returnNo;

    // ── Denormalized for fast query (copied from VatRegistration in Service) ─
    @Column(name = "bin_no", nullable = false, length = 20)
    private String binNo;

    @Column(name = "tin_number", nullable = false, length = 30)
    private String tinNumber;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    // ── Period ─────────────────────────────────────────────────────────────
    @Column(name = "return_period", nullable = false, length = 20)
    private String returnPeriod;   // Monthly | Quarterly | Annually

    @Column(name = "period_month", nullable = false, length = 15)
    private String periodMonth;    // January ... December | Q1 ... Q4

    @Column(name = "period_year", nullable = false, length = 6)
    private String periodYear;

    @Column(name = "assessment_year", length = 10)
    private String assessmentYear;

    // ── Supplies ───────────────────────────────────────────────────────────
    @Column(name = "taxable_supplies", nullable = false)
    private Double taxableSupplies = 0.0;

    @Column(name = "exempt_supplies", nullable = false)
    private Double exemptSupplies = 0.0;

    @Column(name = "zero_rated_supplies", nullable = false)
    private Double zeroRatedSupplies = 0.0;

    // Auto-calculated in Service: taxable + exempt + zeroRated
    @Column(name = "total_supplies", nullable = false)
    private Double totalSupplies = 0.0;

    // ── Tax ────────────────────────────────────────────────────────────────
    @Column(name = "output_tax", nullable = false)
    private Double outputTax = 0.0;

    @Column(name = "input_tax", nullable = false)
    private Double inputTax = 0.0;

    // Auto-calculated in Service: max(0, outputTax - inputTax)
    @Column(name = "net_tax_payable", nullable = false)
    private Double netTaxPayable = 0.0;

    @Column(name = "tax_paid", nullable = false)
    private Double taxPaid = 0.0;

    // ── Dates ──────────────────────────────────────────────────────────────
    @Column(name = "submission_date")
    private LocalDate submissionDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // ── Status & Meta ──────────────────────────────────────────────────────
    @Column(nullable = false, length = 20)
    private String status = "Draft";

    @Column(name = "submitted_by", length = 50)
    private String submittedBy;

    @Column(length = 500)
    private String remarks;

    // ── Soft Delete ────────────────────────────────────────────────────────
    @Column(nullable = false)
    private boolean isDeleted = false;

    // ── Relationship: VatRegistration ──────────────────────────────────────
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_registration_id", nullable = false)
    private VatRegistration vatRegistration;

    // Angular sends this @Transient id; Service resolves it to the entity above
    @Transient
    private Long vatRegistrationId;

    // ── Getters & Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReturnNo() { return returnNo; }
    public void setReturnNo(String returnNo) { this.returnNo = returnNo; }

    public String getBinNo() { return binNo; }
    public void setBinNo(String binNo) { this.binNo = binNo; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getReturnPeriod() { return returnPeriod; }
    public void setReturnPeriod(String returnPeriod) { this.returnPeriod = returnPeriod; }

    public String getPeriodMonth() { return periodMonth; }
    public void setPeriodMonth(String periodMonth) { this.periodMonth = periodMonth; }

    public String getPeriodYear() { return periodYear; }
    public void setPeriodYear(String periodYear) { this.periodYear = periodYear; }

    public String getAssessmentYear() { return assessmentYear; }
    public void setAssessmentYear(String assessmentYear) { this.assessmentYear = assessmentYear; }

    public Double getTaxableSupplies() { return taxableSupplies; }
    public void setTaxableSupplies(Double v) { this.taxableSupplies = v; }

    public Double getExemptSupplies() { return exemptSupplies; }
    public void setExemptSupplies(Double v) { this.exemptSupplies = v; }

    public Double getZeroRatedSupplies() { return zeroRatedSupplies; }
    public void setZeroRatedSupplies(Double v) { this.zeroRatedSupplies = v; }

    public Double getTotalSupplies() { return totalSupplies; }
    public void setTotalSupplies(Double v) { this.totalSupplies = v; }

    public Double getOutputTax() { return outputTax; }
    public void setOutputTax(Double v) { this.outputTax = v; }

    public Double getInputTax() { return inputTax; }
    public void setInputTax(Double v) { this.inputTax = v; }

    public Double getNetTaxPayable() { return netTaxPayable; }
    public void setNetTaxPayable(Double v) { this.netTaxPayable = v; }

    public Double getTaxPaid() { return taxPaid; }
    public void setTaxPaid(Double v) { this.taxPaid = v; }

    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public VatRegistration getVatRegistration() { return vatRegistration; }
    public void setVatRegistration(VatRegistration vatRegistration) {
        this.vatRegistration = vatRegistration;
    }

    public Long getVatRegistrationId() { return vatRegistrationId; }
    public void setVatRegistrationId(Long vatRegistrationId) {
        this.vatRegistrationId = vatRegistrationId;
    }
}
