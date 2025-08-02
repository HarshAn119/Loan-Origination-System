package com.turno.los.service;

import com.turno.los.dto.AgentDecisionRequest;
import com.turno.los.entity.Agent;
import com.turno.los.entity.Loan;
import com.turno.los.enums.AgentDecision;
import com.turno.los.enums.LoanStatus;
import com.turno.los.notification.NotificationService;
import com.turno.los.repository.AgentRepository;
import com.turno.los.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service class for agent-related business operations.
 * Handles agent management, loan assignments, and decision processing.
 * 
 * @author Turno Development Team
 */
@Service
@Transactional
public class AgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    
    private final AgentRepository agentRepository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;
    
    @Autowired
    public AgentService(AgentRepository agentRepository, 
                       LoanRepository loanRepository, 
                       NotificationService notificationService) {
        this.agentRepository = agentRepository;
        this.loanRepository = loanRepository;
        this.notificationService = notificationService;
    }
    
    /**
     * Assign a loan to an available agent.
     * 
     * @param loan The loan to assign
     * @return The ID of the assigned agent, or null if no agent available
     */
    public Long assignLoanToAvailableAgent(Loan loan) {
        logger.info("Attempting to assign loan {} to an available agent", loan.getLoanId());
        
        // Find available agents who can handle this loan amount
        List<Agent> availableAgents = agentRepository.findAvailableAgentsForLoanAmount(
            loan.getLoanAmount().doubleValue()
        );
        
        if (availableAgents.isEmpty()) {
            logger.warn("No available agents found for loan amount: {}", loan.getLoanAmount());
            return null;
        }
        
        // Simple round-robin assignment (in a real system, you might use more sophisticated logic)
        Agent selectedAgent = selectBestAgent(availableAgents, loan);
        
        if (selectedAgent != null) {
            logger.info("Assigned loan {} to agent {} ({})", 
                       loan.getLoanId(), selectedAgent.getName(), selectedAgent.getAgentId());
            return selectedAgent.getId();
        }
        
        return null;
    }
    
    /**
     * Select the best agent for a loan assignment.
     * 
     * @param availableAgents List of available agents
     * @param loan The loan to assign
     * @return The selected agent
     */
    private Agent selectBestAgent(List<Agent> availableAgents, Loan loan) {
        // Simple selection logic - in a real system, this would be more sophisticated
        // Consider factors like agent workload, specialization, performance, etc.
        
        // Filter agents by specialization if applicable
        List<Agent> specializedAgents = availableAgents.stream()
                .filter(agent -> agent.getSpecializations() != null && 
                        agent.getSpecializations().toLowerCase().contains(loan.getLoanType().name().toLowerCase()))
                .toList();
        
        List<Agent> candidates = specializedAgents.isEmpty() ? availableAgents : specializedAgents;
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Select randomly from candidates (simple load balancing)
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }
    
    /**
     * Send notifications for loan assignment.
     * 
     * @param loan The loan that was assigned
     */
    @Async("notificationExecutor")
    public void sendAssignmentNotifications(Loan loan) {
        if (loan.getAssignedAgentId() == null) {
            return;
        }
        
        Optional<Agent> agentOpt = agentRepository.findById(loan.getAssignedAgentId());
        if (agentOpt.isEmpty()) {
            logger.warn("Agent not found for ID: {}", loan.getAssignedAgentId());
            return;
        }
        
        Agent agent = agentOpt.get();
        
        // Send notification to the assigned agent
        notificationService.sendLoanAssignmentNotification(agent, loan);
        
        // Send notification to the agent's manager if they have one
        if (agent.getManager() != null) {
            notificationService.sendManagerNotification(agent.getManager(), agent, loan);
        }
    }
    
    /**
     * Process agent decision on a loan.
     * 
     * @param agentId The agent ID
     * @param loanId The loan ID
     * @param decisionRequest The agent's decision
     * @return True if decision was processed successfully
     */
    public boolean processAgentDecision(Long agentId, String loanId, AgentDecisionRequest decisionRequest) {
        logger.info("Processing agent decision for loan: {} by agent: {}", loanId, agentId);
        
        // Find the loan
        Optional<Loan> loanOpt = loanRepository.findByLoanId(loanId);
        if (loanOpt.isEmpty()) {
            logger.error("Loan not found: {}", loanId);
            return false;
        }
        
        Loan loan = loanOpt.get();
        
        // Verify the loan is assigned to this agent
        if (!agentId.equals(loan.getAssignedAgentId())) {
            logger.error("Loan {} is not assigned to agent {}", loanId, agentId);
            return false;
        }
        
        // Verify the loan is under review
        if (loan.getStatus() != LoanStatus.UNDER_REVIEW) {
            logger.error("Loan {} is not under review. Current status: {}", loanId, loan.getStatus());
            return false;
        }
        
        // Apply the agent's decision
        LoanStatus newStatus = decisionRequest.getDecision().toLoanStatus();
        loan.setStatus(newStatus);
        
        // Set decision reason
        String reason = decisionRequest.getReason();
        if (reason != null && !reason.trim().isEmpty()) {
            loan.setDecisionReason(reason);
        } else {
            loan.setDecisionReason("Decision made by agent: " + decisionRequest.getDecision().getDisplayName());
        }
        
        // Save the updated loan
        loanRepository.save(loan);
        
        // Send customer notification based on decision
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
                reason
            );
        }
        
        logger.info("Agent decision processed successfully. Loan: {}, Decision: {}, Status: {}", 
                   loanId, decisionRequest.getDecision(), newStatus);
        
        return true;
    }
    
    /**
     * Get agent by ID.
     * 
     * @param id The agent ID
     * @return Optional containing the agent if found
     */
    @Transactional(readOnly = true)
    public Optional<Agent> getAgentById(Long id) {
        return agentRepository.findById(id);
    }
    
    /**
     * Get agent by agent ID.
     * 
     * @param agentId The agent ID
     * @return Optional containing the agent if found
     */
    @Transactional(readOnly = true)
    public Optional<Agent> getAgentByAgentId(String agentId) {
        return agentRepository.findByAgentId(agentId);
    }
    
    /**
     * Get all active agents.
     * 
     * @return List of active agents
     */
    @Transactional(readOnly = true)
    public List<Agent> getActiveAgents() {
        return agentRepository.findByStatus(Agent.AgentStatus.ACTIVE);
    }
    
    /**
     * Create a new agent.
     * 
     * @param agentId The agent ID
     * @param name The agent name
     * @param email The agent email
     * @return The created agent
     */
    public Agent createAgent(String agentId, String name, String email) {
        Agent agent = new Agent(agentId, name, email);
        return agentRepository.save(agent);
    }
} 