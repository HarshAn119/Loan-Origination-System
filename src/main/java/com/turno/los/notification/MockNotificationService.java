package com.turno.los.notification;

import com.turno.los.entity.Agent;
import com.turno.los.entity.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class MockNotificationService implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockNotificationService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Value("${los.notification.enabled:true}")
    private boolean notificationsEnabled;
    
    @Value("${los.notification.push-notification-enabled:true}")
    private boolean pushNotificationsEnabled;
    
    @Value("${los.notification.sms-enabled:true}")
    private boolean smsEnabled;
    
    @Override
    public void sendLoanAssignmentNotification(Agent agent, Loan loan) {
        if (!notificationsEnabled || !pushNotificationsEnabled) {
            return;
        }
        
        String message = String.format(
            "[PUSH NOTIFICATION] Loan assignment notification sent to Agent %s (%s) at %s\n" +
            "Loan ID: %s\n" +
            "Customer: %s\n" +
            "Amount: $%,.2f\n" +
            "Type: %s",
            agent.getName(),
            agent.getEmail(),
            LocalDateTime.now().format(formatter),
            loan.getLoanId(),
            loan.getCustomerName(),
            loan.getLoanAmount(),
            loan.getLoanType()
        );
        
        logger.info(message);
    }
    
    @Override
    public void sendManagerNotification(Agent manager, Agent agent, Loan loan) {
        if (!notificationsEnabled || !pushNotificationsEnabled) {
            return;
        }
        
        String message = String.format(
            "[PUSH NOTIFICATION] Manager notification sent to %s (%s) at %s\n" +
            "Agent %s has been assigned loan %s\n" +
            "Customer: %s\n" +
            "Amount: $%,.2f",
            manager.getName(),
            manager.getEmail(),
            LocalDateTime.now().format(formatter),
            agent.getName(),
            loan.getLoanId(),
            loan.getCustomerName(),
            loan.getLoanAmount()
        );
        
        logger.info(message);
    }
    
    @Override
    public void sendLoanApprovalSMS(String customerPhone, String customerName, Loan loan) {
        if (!notificationsEnabled || !smsEnabled) {
            return;
        }
        
        String message = String.format(
            "[SMS] Loan approval notification sent to %s (%s) at %s\n" +
            "Loan ID: %s\n" +
            "Status: %s\n" +
            "Amount: $%,.2f\n" +
            "Message: Congratulations! Your loan application has been approved.",
            customerName,
            customerPhone,
            LocalDateTime.now().format(formatter),
            loan.getLoanId(),
            loan.getStatus(),
            loan.getLoanAmount()
        );
        
        logger.info(message);
    }
    
    @Override
    public void sendLoanRejectionSMS(String customerPhone, String customerName, Loan loan, String reason) {
        if (!notificationsEnabled || !smsEnabled) {
            return;
        }
        
        String message = String.format(
            "[SMS] Loan rejection notification sent to %s (%s) at %s\n" +
            "Loan ID: %s\n" +
            "Status: %s\n" +
            "Amount: $%,.2f\n" +
            "Reason: %s\n" +
            "Message: We regret to inform you that your loan application has been rejected.",
            customerName,
            customerPhone,
            LocalDateTime.now().format(formatter),
            loan.getLoanId(),
            loan.getStatus(),
            loan.getLoanAmount(),
            reason != null ? reason : "No specific reason provided"
        );
        
        logger.info(message);
    }
    
    @Override
    public void sendProcessingStartedNotification(Loan loan) {
        if (!notificationsEnabled) {
            return;
        }
        
        String message = String.format(
            "[SYSTEM] Loan processing started at %s\n" +
            "Loan ID: %s\n" +
            "Customer: %s\n" +
            "Amount: $%,.2f",
            LocalDateTime.now().format(formatter),
            loan.getLoanId(),
            loan.getCustomerName(),
            loan.getLoanAmount()
        );
        
        logger.info(message);
    }
    
    @Override
    public void sendProcessingCompletedNotification(Loan loan) {
        if (!notificationsEnabled) {
            return;
        }
        
        String message = String.format(
            "[SYSTEM] Loan processing completed at %s\n" +
            "Loan ID: %s\n" +
            "Customer: %s\n" +
            "Final Status: %s\n" +
            "Amount: $%,.2f",
            LocalDateTime.now().format(formatter),
            loan.getLoanId(),
            loan.getCustomerName(),
            loan.getStatus(),
            loan.getLoanAmount()
        );
        
        logger.info(message);
    }
}