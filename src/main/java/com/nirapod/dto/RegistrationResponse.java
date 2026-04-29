package com.nirapod.dto;

public class RegistrationResponse {

    private Integer userId;
    private Long    taxpayerId;
    private String  tinNumber;
    private String  fullName;
    private String  email;
    private String  accountType;
    private String  message;

    public RegistrationResponse() {}

    public RegistrationResponse(
            Integer userId, Long taxpayerId, String tinNumber,
            String fullName, String email, String accountType, String message) {
        this.userId      = userId;
        this.taxpayerId  = taxpayerId;
        this.tinNumber   = tinNumber;
        this.fullName    = fullName;
        this.email       = email;
        this.accountType = accountType;
        this.message     = message;
    }

    public Integer getUserId()        { return userId; }
    public Long    getTaxpayerId()    { return taxpayerId; }
    public String  getTinNumber()     { return tinNumber; }
    public String  getFullName()      { return fullName; }
    public String  getEmail()         { return email; }
    public String  getAccountType()   { return accountType; }
    public String  getMessage()       { return message; }
}
