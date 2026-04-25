package com.nirapod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "itr_action_history")
public class ITRAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String status;
    private String remarks;
    
    @Column(name = "from_status")
    private String fromStatus;

    @Column(name = "to_status")  
    private String toStatus;
    
    @Column(name = "performed_by")
    private String performedBy;
    
    private String role;
    
    
    private LocalDateTime performedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id")
    @JsonIgnore
    private IncomeTaxReturn incomeTaxReturn;

    // --- Manual Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public IncomeTaxReturn getIncomeTaxReturn() { return incomeTaxReturn; }
    public void setIncomeTaxReturn(IncomeTaxReturn incomeTaxReturn) { this.incomeTaxReturn = incomeTaxReturn; }
    
    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
	public String getFromStatus() {
		return fromStatus;
	}
	public void setFromStatus(String fromStatus) {
		this.fromStatus = fromStatus;
	}
	public String getToStatus() {
		return toStatus;
	}
	public void setToStatus(String toStatus) {
		this.toStatus = toStatus;
	}
	public LocalDateTime getPerformedAt() {
		return performedAt;
	}
    
}