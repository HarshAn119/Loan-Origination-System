package com.turno.los.dto;

import com.turno.los.enums.LoanType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public class LoanApplicationRequest {
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;
    
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String customerPhone;
    
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than 0")
    @DecimalMax(value = "999999999.99", message = "Loan amount must not exceed 999,999,999.99")
    private BigDecimal loanAmount;
    
    @NotNull(message = "Loan type is required")
    private LoanType loanType;
    
    public LoanApplicationRequest() {}
    
    public LoanApplicationRequest(String customerName, String customerPhone, 
                                BigDecimal loanAmount, LoanType loanType) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
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
    
    @Override
    public String toString() {
        return "LoanApplicationRequest{" +
                "customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", loanAmount=" + loanAmount +
                ", loanType=" + loanType +
                '}';
    }
}