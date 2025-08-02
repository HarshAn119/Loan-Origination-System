package com.turno.los.service;

import com.turno.los.dto.LoanApplicationRequest;
import com.turno.los.dto.LoanResponse;
import com.turno.los.entity.Loan;
import com.turno.los.enums.LoanStatus;
import com.turno.los.enums.LoanType;
import com.turno.los.notification.NotificationService;
import com.turno.los.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@Transactional
public class LoanService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);
    
    private final LoanRepository loanRepository;
    private final AgentService agentService;
    private final NotificationService notificationService;
    
    // Thread-safe map to track loans being processed
    private final Map<String, Boolean> processingLoans = new ConcurrentHashMap<>();
    
    @Autowired
    public LoanService(LoanRepository loanRepository, 
                      AgentService agentService, 
                      NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.agentService = agentService;
        this.notificationService = notificationService;
    }
    
    public LoanResponse submitLoanApplication(LoanApplicationRequest request) {
        logger.info("Submitting loan application for customer: {}", request.getCustomerName());
        
        // Generate unique loan ID
        String loanId = generateLoanId();
        
        // Create loan entity
        Loan loan = new Loan(
            loanId,
            request.getCustomerName(),
            request.getCustomerPhone(),
            request.getLoanAmount(),
            request.getLoanType()
        );
        
        // Save loan
        Loan savedLoan = loanRepository.save(loan);
        
        logger.info("Loan application submitted successfully. Loan ID: {}", loanId);
        
        return convertToResponse(savedLoan);
    }
    
    @Transactional(readOnly = true)
    public Optional<LoanResponse> getLoanById(Long id) {
        return loanRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    @Transactional(readOnly = true)
    public Optional<LoanResponse> getLoanByLoanId(String loanId) {
        return loanRepository.findByLoanId(loanId)
                .map(this::convertToResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<LoanResponse> getLoansByStatus(LoanStatus status, Pageable pageable) {
        return loanRepository.findByStatus(status, pageable)
                .map(this::convertToResponse);
    }
    
    @Transactional(readOnly = true)
    public Map<LoanStatus, Long> getStatusCount() {
        Map<LoanStatus, Long> statusCount = new ConcurrentHashMap<>();
        
        for (LoanStatus status : LoanStatus.values()) {
            long count = loanRepository.countByStatus(status);
            statusCount.put(status, count);
        }
        
        return statusCount;
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopCustomers() {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 3);
        List<Object[]> results = loanRepository.findTopCustomersByApprovedLoans(pageable);
        
        return results.stream()
                .map(result -> {
                    Map<String, Object> customer = new ConcurrentHashMap<>();
                    customer.put("customerName", result[0]);
                    customer.put("approvedCount", result[1]);
                    return customer;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Process loans that are ready for automated processing.
     * This method is called by the scheduler.
     */
    @Async("loanProcessingExecutor")
    public void processLoansReadyForProcessing() {
        List<Loan> loansToProcess = loanRepository.findLoansReadyForProcessing();
        
        for (Loan loan : loansToProcess) {
            // Check if loan is already being processed
            if (processingLoans.putIfAbsent(loan.getLoanId(), true) == null) {
                try {
                    processLoan(loan);
                } finally {
                    processingLoans.remove(loan.getLoanId());
                }
            }
        }
    }
    
    private void processLoan(Loan loan) {
        logger.info("Starting processing for loan: {}", loan.getLoanId());
        
        try {
            loan.setProcessingStartedAt(LocalDateTime.now());
            loanRepository.save(loan);
            
            notificationService.sendProcessingStartedNotification(loan);
            
            simulateProcessingDelay();
            
            LoanStatus newStatus = applyBusinessRules(loan);
            loan.setStatus(newStatus);
            
            if (newStatus == LoanStatus.UNDER_REVIEW) {
                assignLoanToAgent(loan);
            }
            
            loan.setProcessingCompletedAt(LocalDateTime.now());
            loanRepository.save(loan);
            
            notificationService.sendProcessingCompletedNotification(loan);
            
            if (newStatus.isApproved()) {
                notificationService.sendLoanApprovalSMS(
                    loan.getCustomerPhone(), 
                    loan.getCustomerName(), 
                    loan
                );
            } else if (newStatus.isRejected()) {
                notificationService.sendLoanRejectionSMS(
                    loan.getCustomerPhone(), 
                    loan.getCustomerName(), 
                    loan, 
                    loan.getDecisionReason()
                );
            }
            
            logger.info("Completed processing for loan: {}. Final status: {}", 
                       loan.getLoanId(), newStatus);
            
        } catch (Exception e) {
            logger.error("Error processing loan: {}", loan.getLoanId(), e);
            // In a real system, you might want to retry or mark as failed
        }
    }

    private LoanStatus applyBusinessRules(Loan loan) {
        BigDecimal amount = loan.getLoanAmount();
        LoanType type = loan.getLoanType();
        
        // Simple business rules for demonstration
        // In a real system, these would be more complex
        
        // Rule 1: Auto loans over $50,000 require review
        if (type == LoanType.AUTO && amount.compareTo(new BigDecimal("50000")) > 0) {
            loan.setDecisionReason("Auto loan amount exceeds automatic approval limit");
            return LoanStatus.UNDER_REVIEW;
        }
        
        // Rule 2: Business loans over $100,000 require review
        if (type == LoanType.BUSINESS && amount.compareTo(new BigDecimal("100000")) > 0) {
            loan.setDecisionReason("Business loan amount exceeds automatic approval limit");
            return LoanStatus.UNDER_REVIEW;
        }
        
        // Rule 3: Home loans over $200,000 require review
        if (type == LoanType.HOME && amount.compareTo(new BigDecimal("200000")) > 0) {
            loan.setDecisionReason("Home loan amount exceeds automatic approval limit");
            return LoanStatus.UNDER_REVIEW;
        }
        
        // Rule 4: Personal loans over $25,000 require review
        if (type == LoanType.PERSONAL && amount.compareTo(new BigDecimal("25000")) > 0) {
            loan.setDecisionReason("Personal loan amount exceeds automatic approval limit");
            return LoanStatus.UNDER_REVIEW;
        }
        
        // Rule 5: Very small amounts might be rejected
        if (amount.compareTo(new BigDecimal("1000")) < 0) {
            loan.setDecisionReason("Loan amount too small for processing");
            return LoanStatus.REJECTED_BY_SYSTEM;
        }
        
        // Rule 6: Very large amounts might be rejected
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            loan.setDecisionReason("Loan amount exceeds maximum limit");
            return LoanStatus.REJECTED_BY_SYSTEM;
        }
        
        // Default: Approve the loan
        loan.setDecisionReason("Loan meets automatic approval criteria");
        return LoanStatus.APPROVED_BY_SYSTEM;
    }
    
    private void assignLoanToAgent(Loan loan) {
        try {
            Long agentId = agentService.assignLoanToAvailableAgent(loan);
            if (agentId != null) {
                loan.setAssignedAgentId(agentId);
                loanRepository.save(loan);
                
                // Send notifications
                agentService.sendAssignmentNotifications(loan);
                
                logger.info("Loan {} assigned to agent {}", loan.getLoanId(), agentId);
            } else {
                logger.warn("No available agent found for loan: {}", loan.getLoanId());
            }
        } catch (Exception e) {
            logger.error("Error assigning loan to agent: {}", loan.getLoanId(), e);
        }
    }
    
    /**
     * Simulate processing delay.
     */
    private void simulateProcessingDelay() {
        try {
            // Random delay between 20-30 seconds
            long delay = (long) (Math.random() * 10000) + 20000; // 20-30 seconds
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Processing delay interrupted");
        }
    }

    private String generateLoanId() {
        return "LOAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private LoanResponse convertToResponse(Loan loan) {
        return new LoanResponse(
            loan.getId(),
            loan.getLoanId(),
            loan.getCustomerName(),
            loan.getCustomerPhone(),
            loan.getLoanAmount(),
            loan.getLoanType(),
            loan.getStatus(),
            loan.getAssignedAgentId(),
            loan.getProcessingStartedAt(),
            loan.getProcessingCompletedAt(),
            loan.getDecisionReason(),
            loan.getCreatedAt(),
            loan.getUpdatedAt()
        );
    }
} 