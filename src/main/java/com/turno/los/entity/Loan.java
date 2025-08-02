package com.turno.los.entity;

import com.turno.los.enums.LoanStatus;
import com.turno.los.enums.LoanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "loans", indexes = {
    @Index(name = "idx_loan_status", columnList = "status"),
    @Index(name = "idx_loan_customer_name", columnList = "customer_name"),
    @Index(name = "idx_loan_created_at", columnList = "created_at")
})
public class Loan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "loan_id", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Loan ID is required")
    @Size(max = 50, message = "Loan ID must not exceed 50 characters")
    private String loanId;
    
    @Column(name = "customer_name", nullable = false, length = 100)
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;
    
    @Column(name = "customer_phone", nullable = false, length = 20)
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String customerPhone;
    
    @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than 0")
    @DecimalMax(value = "999999999.99", message = "Loan amount must not exceed 999,999,999.99")
    private BigDecimal loanAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false, length = 20)
    @NotNull(message = "Loan type is required")
    private LoanType loanType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Loan status is required")
    private LoanStatus status = LoanStatus.APPLIED;
    
    @Column(name = "assigned_agent_id")
    private Long assignedAgentId;
    
    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;
    
    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;
    
    @Column(name = "decision_reason", length = 500)
    @Size(max = 500, message = "Decision reason must not exceed 500 characters")
    private String decisionReason;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public Loan() {}
    
    public Loan(String loanId, String customerName, String customerPhone, 
                BigDecimal loanAmount, LoanType loanType) {
        this.loanId = loanId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
        this.status = LoanStatus.APPLIED;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id) && Objects.equals(loanId, loan.loanId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, loanId);
    }
    
    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", loanId='" + loanId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", loanAmount=" + loanAmount +
                ", loanType=" + loanType +
                '}';
    }
}