package com.turno.los.dto;

import com.turno.los.enums.AgentDecision;
import jakarta.validation.constraints.NotNull;


public class AgentDecisionRequest {
    
    @NotNull(message = "Decision is required")
    private AgentDecision decision;
    
    private String reason;
    
    public AgentDecisionRequest() {}
    
    public AgentDecisionRequest(AgentDecision decision) {
        this.decision = decision;
    }
    
    public AgentDecisionRequest(AgentDecision decision, String reason) {
        this.decision = decision;
        this.reason = reason;
    }
    
    public AgentDecision getDecision() {
        return decision;
    }
    
    public void setDecision(AgentDecision decision) {
        this.decision = decision;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public String toString() {
        return "AgentDecisionRequest{" +
                "decision=" + decision +
                ", reason='" + reason + '\'' +
                '}';
    }
}