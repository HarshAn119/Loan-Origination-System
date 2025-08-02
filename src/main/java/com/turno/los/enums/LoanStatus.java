package com.turno.los.enums;


public enum LoanStatus {
    
    APPLIED("Applied"),
    
    APPROVED_BY_SYSTEM("Approved by System"),
    
    REJECTED_BY_SYSTEM("Rejected by System"),
    
    UNDER_REVIEW("Under Review"),
    
    APPROVED_BY_AGENT("Approved by Agent"),
    
    REJECTED_BY_AGENT("Rejected by Agent");
    
    private final String displayName;
    
    LoanStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isApproved() {
        return this == APPROVED_BY_SYSTEM || this == APPROVED_BY_AGENT;
    }
    
    public boolean isRejected() {
        return this == REJECTED_BY_SYSTEM || this == REJECTED_BY_AGENT;
    }
    
    public boolean isFinal() {
        return isApproved() || isRejected();
    }
    
    public boolean requiresAgentReview() {
        return this == UNDER_REVIEW;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}