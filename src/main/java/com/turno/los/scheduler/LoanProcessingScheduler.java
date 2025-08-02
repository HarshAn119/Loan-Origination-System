package com.turno.los.scheduler;

import com.turno.los.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for background loan processing tasks.
 */
@Component
public class LoanProcessingScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanProcessingScheduler.class);
    
    private final LoanService loanService;
    
    @Autowired
    public LoanProcessingScheduler(LoanService loanService) {
        this.loanService = loanService;
    }
    
    /**
     * Scheduled task to process loans that are ready for automated processing.
     * Runs every 30 seconds.
     */
    @Scheduled(fixedDelay = 30000) // 30 seconds
    public void processLoansReadyForProcessing() {
        try {
            logger.debug("Starting scheduled loan processing task");
            loanService.processLoansReadyForProcessing();
            logger.debug("Completed scheduled loan processing task");
        } catch (Exception e) {
            logger.error("Error in scheduled loan processing task", e);
        }
    }
    
    /**
     * Scheduled task to log system status.
     * Runs every 5 minutes.
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void logSystemStatus() {
        try {
            logger.info("=== System Status Report ===");
            
            // Get status counts
            var statusCount = loanService.getStatusCount();
            statusCount.forEach((status, count) -> 
                logger.info("Loans with status {}: {}", status, count)
            );
            
            logger.info("=== End System Status Report ===");
        } catch (Exception e) {
            logger.error("Error logging system status", e);
        }
    }
} 