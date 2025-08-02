package com.turno.los.repository;

import com.turno.los.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    
    
    Optional<Agent> findByAgentId(String agentId);
    
    
    Optional<Agent> findByEmail(String email);
    
    
    List<Agent> findByStatus(Agent.AgentStatus status);
    
    @Query("SELECT a FROM Agent a WHERE a.status = 'ACTIVE' AND (a.maxLoanAmount IS NULL OR a.maxLoanAmount >= :loanAmount)")
    List<Agent> findAvailableAgentsForLoanAmount(@Param("loanAmount") Double loanAmount);
    
    @Query("SELECT a FROM Agent a WHERE a.status = 'ACTIVE' AND a.id NOT IN " +
           "(SELECT DISTINCT l.assignedAgentId FROM Loan l WHERE l.status = 'UNDER_REVIEW' AND l.assignedAgentId IS NOT NULL)")
    List<Agent> findAvailableAgents();
    
    long countByStatus(Agent.AgentStatus status);
}