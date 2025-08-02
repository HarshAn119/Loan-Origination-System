package com.turno.los.enums;


public enum LoanType {
    
    PERSONAL("Personal Loan"),
    
    HOME("Home Loan"),
    
    AUTO("Auto Loan"),
    
    BUSINESS("Business Loan");
    
    private final String displayName;
    
    LoanType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}