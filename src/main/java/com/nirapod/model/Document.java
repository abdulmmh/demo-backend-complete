package com.nirapod.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "document")
@Table(name = "documents")
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String documentNo;

    private String tinNumber;
    private String taxpayerName;
    private String documentType;
    private String documentCategory;
    private String documentTitle;
    private String referenceNo;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private LocalDate submissionDate;
    private LocalDate verificationDate;
    private String fileSize;
    private String status;
    private String verifiedBy;

    @Column(length = 2000)
    private String remarks;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "Pending";
        if (this.documentNo == null || this.documentNo.isEmpty())
            this.documentNo = "DOC-" + System.currentTimeMillis();
    }

}
