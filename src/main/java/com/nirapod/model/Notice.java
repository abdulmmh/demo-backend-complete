package com.nirapod.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "notice")
@Table(name = "notices")
@Setter
@Getter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String noticeNo;

    private String subject;

    @Column(length = 5000)
    private String body;

    private String noticeType;
    private String priority;
    private String targetType;
    private String tinNumber;
    private String taxpayerName;
    private String issuedBy;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private LocalDate readDate;
    private LocalDate responseDate;

    @Column(length = 2000)
    private String responseNote;

    private String attachmentName;
    private String status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "Unread";
        if (this.noticeNo == null || this.noticeNo.isEmpty())
            this.noticeNo = "NTC-" + System.currentTimeMillis();
    }

}
