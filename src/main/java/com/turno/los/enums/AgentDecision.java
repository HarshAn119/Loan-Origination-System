package com.turno.los.enums;

public enum AgentDecision {
    
    APPROVE("Approve"),
    
    REJECT("Reject");
    
    private final String displayName;
    
    AgentDecision(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public LoanStatus toLoanStatus() {
        return switch (this) {
            case APPROVE -> LoanStatus.APPROVED_BY_AGENT;
            case REJECT -> LoanStatus.REJECTED_BY_AGENT;
        };
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}