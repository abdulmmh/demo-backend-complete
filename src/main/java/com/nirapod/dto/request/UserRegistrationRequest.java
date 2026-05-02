package com.nirapod.dto.request;


public class UserRegistrationRequest {

    // ── Common ────────────────────────────────────────────────────────────────
	private Long   taxpayerTypeId; 
	private String accountCategory;   
    private String fullName;
    private String email;
    private String phone;
    private String password;      

    // ── Individual-specific ───────────────────────────────────────────────────
    private String nid;
    private String dateOfBirth;   
    private String gender;
    private String profession;

    // ── Company-specific ──────────────────────────────────────────────────────
    private String companyName;
    private String rjscNo;
    private String incorporationDate;   
    private String natureOfBusiness;
    private String authorizedPersonName;
    private String authorizedPersonNid;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    

    public String getFullName()              { return fullName; }
    
    public Long getTaxpayerTypeId() {
		return taxpayerTypeId;
	}
	public void setTaxpayerTypeId(Long taxpayerTypeId) {
		this.taxpayerTypeId = taxpayerTypeId;
	}
	public String getAccountCategory() {
		return accountCategory;
	}
	public void setAccountCategory(String accountCategory) {
		this.accountCategory = accountCategory;
	}
	public void   setFullName(String v)      { this.fullName = v; }

    public String getEmail()                 { return email; }
    public void   setEmail(String v)         { this.email = v; }

    public String getPhone()                 { return phone; }
    public void   setPhone(String v)         { this.phone = v; }

    public String getPassword()              { return password; }
    public void   setPassword(String v)      { this.password = v; }

    public String getNid()                   { return nid; }
    public void   setNid(String v)           { this.nid = v; }

    public String getDateOfBirth()           { return dateOfBirth; }
    public void   setDateOfBirth(String v)   { this.dateOfBirth = v; }

    public String getGender()                { return gender; }
    public void   setGender(String v)        { this.gender = v; }

    public String getProfession()            { return profession; }
    public void   setProfession(String v)    { this.profession = v; }

    public String getCompanyName()           { return companyName; }
    public void   setCompanyName(String v)   { this.companyName = v; }

    public String getRjscNo()                { return rjscNo; }
    public void   setRjscNo(String v)        { this.rjscNo = v; }

    public String getIncorporationDate()         { return incorporationDate; }
    public void   setIncorporationDate(String v) { this.incorporationDate = v; }

    public String getNatureOfBusiness()          { return natureOfBusiness; }
    public void   setNatureOfBusiness(String v)  { this.natureOfBusiness = v; }

    public String getAuthorizedPersonName()         { return authorizedPersonName; }
    public void   setAuthorizedPersonName(String v) { this.authorizedPersonName = v; }

    public String getAuthorizedPersonNid()          { return authorizedPersonNid; }
    public void   setAuthorizedPersonNid(String v)  { this.authorizedPersonNid = v; }
}
