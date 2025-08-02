package com.turno.los.controller;

import com.turno.los.dto.LoanApplicationRequest;
import com.turno.los.dto.LoanResponse;
import com.turno.los.enums.LoanStatus;
import com.turno.los.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/loans")
@Tag(name = "Loan Management", description = "APIs for loan application and management")
public class LoanController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);
    
    private final LoanService loanService;
    
    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }
    
    /**
     * Submit a new loan application.
     * 
     * @param request The loan application request
     * @return The created loan response
     */
    @PostMapping
    @Operation(summary = "Submit loan application", 
               description = "Submit a new loan application for processing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Loan application submitted successfully",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoanResponse> submitLoanApplication(
            @Valid @RequestBody LoanApplicationRequest request) {
        
        logger.info("Received loan application request for customer: {}", request.getCustomerName());
        
        try {
            LoanResponse response = loanService.submitLoanApplication(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error submitting loan application", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get loan by ID.
     * 
     * @param id The loan ID
     * @return The loan response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get loan by ID", 
               description = "Retrieve a loan application by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan found",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoanResponse> getLoanById(
            @Parameter(description = "Loan ID") @PathVariable Long id) {
        
        Optional<LoanResponse> loan = loanService.getLoanById(id);
        return loan.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get loan by loan ID.
     * 
     * @param loanId The loan ID
     * @return The loan response
     */
    @GetMapping("/by-loan-id/{loanId}")
    @Operation(summary = "Get loan by loan ID", 
               description = "Retrieve a loan application by its loan ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan found",
                    content = @Content(schema = @Schema(implementation = LoanResponse.class))),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoanResponse> getLoanByLoanId(
            @Parameter(description = "Loan ID") @PathVariable String loanId) {
        
        Optional<LoanResponse> loan = loanService.getLoanByLoanId(loanId);
        return loan.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get loans by status with pagination.
     * 
     * @param status The loan status
     * @param page Page number (0-based)
     * @param size Page size
     * @return Page of loan responses
     */
    @GetMapping
    @Operation(summary = "Get loans by status", 
               description = "Retrieve loans filtered by status with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loans retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<LoanResponse>> getLoansByStatus(
            @Parameter(description = "Loan status") @RequestParam(required = false) LoanStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            if (status != null) {
                Page<LoanResponse> loans = loanService.getLoansByStatus(status, pageable);
                return ResponseEntity.ok(loans);
            } else {
                // Return empty page if no status specified
                return ResponseEntity.ok(Page.empty(pageable));
            }
        } catch (Exception e) {
            logger.error("Error retrieving loans by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get loan status count.
     * 
     * @return Map of status to count
     */
    @GetMapping("/status-count")
    @Operation(summary = "Get loan status count", 
               description = "Retrieve count of loans in each status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status count retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<LoanStatus, Long>> getStatusCount() {
        try {
            Map<LoanStatus, Long> statusCount = loanService.getStatusCount();
            return ResponseEntity.ok(statusCount);
        } catch (Exception e) {
            logger.error("Error retrieving status count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get top customers by approved loan count.
     * 
     * @return List of top customers
     */
    @GetMapping("/customers/top")
    @Operation(summary = "Get top customers", 
               description = "Retrieve top 3 customers with most approved loans")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top customers retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, Object>>> getTopCustomers() {
        try {
            List<Map<String, Object>> topCustomers = loanService.getTopCustomers();
            return ResponseEntity.ok(topCustomers);
        } catch (Exception e) {
            logger.error("Error retrieving top customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 