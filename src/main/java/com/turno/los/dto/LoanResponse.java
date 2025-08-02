package com.turno.los.dto;

import com.turno.los.enums.LoanStatus;
import com.turno.los.enums.LoanType;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public class LoanResponse {
    
    private Long id;
    private String loanId;
    private String customerName;
    private String customerPhone;
    private BigDecimal loanAmount;
    private LoanType loanType;
    private LoanStatus status;
    private Long assignedAgentId;
    private LocalDateTime processingStartedAt;
    private LocalDateTime processingCompletedAt;
    private String decisionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public LoanResponse() {}
    
    public LoanResponse(Long id, String loanId, String customerName, String customerPhone,
                       BigDecimal loanAmount, LoanType loanType, LoanStatus status,
                       Long assignedAgentId, LocalDateTime processingStartedAt,
                       LocalDateTime processingCompletedAt, String decisionReason,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.loanId = loanId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
        this.status = status;
        this.assignedAgentId = assignedAgentId;
        this.processingStartedAt = processingStartedAt;
        this.processingCompletedAt = processingCompletedAt;
        this.decisionReason = decisionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLoanId() {
        return loanId;
    }
    
    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public BigDecimal getLoanAmount() {
        return loanAmount;
    }
    
    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }
    
    public LoanType getLoanType() {
        return loanType;
    }
    
    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }
    
    public LoanStatus getStatus() {
        return status;
    }
    
    public void setStatus(LoanStatus status) {
        this.status = status;
    }
    
    public Long getAssignedAgentId() {
        return assignedAgentId;
    }
    
    public void setAssignedAgentId(Long assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }
    
    public LocalDateTime getProcessingStartedAt() {
        return processingStartedAt;
    }
    
    public void setProcessingStartedAt(LocalDateTime processingStartedAt) {
        this.processingStartedAt = processingStartedAt;
    }
    
    public LocalDateTime getProcessingCompletedAt() {
        return processingCompletedAt;
    }
    
    public void setProcessingCompletedAt(LocalDateTime processingCompletedAt) {
        this.processingCompletedAt = processingCompletedAt;
    }
    
    public String getDecisionReason() {
        return decisionReason;
    }
    
    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "LoanResponse{" +
                "id=" + id +
                ", loanId='" + loanId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", loanAmount=" + loanAmount +
                ", loanType=" + loanType +
                '}';
    }
}