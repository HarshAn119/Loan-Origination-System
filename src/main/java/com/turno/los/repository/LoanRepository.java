package com.turno.los.repository;

import com.turno.los.entity.Loan;
import com.turno.los.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    
    Optional<Loan> findByLoanId(String loanId);
    
    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);
    
    List<Loan> findByStatus(LoanStatus status);
    
    List<Loan> findByAssignedAgentId(Long agentId);
    
    long countByStatus(LoanStatus status);
    
    @Query("SELECT l FROM Loan l WHERE l.status = 'APPLIED' AND l.assignedAgentId IS NULL")
    List<Loan> findLoansReadyForProcessing();
    
    @Query("SELECT l.customerName, COUNT(l) as approvedCount " +
           "FROM Loan l " +
           "WHERE l.status IN ('APPROVED_BY_SYSTEM', 'APPROVED_BY_AGENT') " +
           "GROUP BY l.customerName " +
           "ORDER BY approvedCount DESC")
    List<Object[]> findTopCustomersByApprovedLoans(Pageable pageable);
}