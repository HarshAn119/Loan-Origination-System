package com.turno.los.notification;

import com.turno.los.entity.Agent;
import com.turno.los.entity.Loan;


public interface NotificationService {
    
    
    void sendLoanAssignmentNotification(Agent agent, Loan loan);
    
    void sendManagerNotification(Agent manager, Agent agent, Loan loan);
    
    
    void sendLoanApprovalSMS(String customerPhone, String customerName, Loan loan);
    
    void sendLoanRejectionSMS(String customerPhone, String customerName, Loan loan, String reason);
    
    
    void sendProcessingStartedNotification(Loan loan);
    
    void sendProcessingCompletedNotification(Loan loan);
}