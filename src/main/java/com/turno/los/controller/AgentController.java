package com.turno.los.controller;

import com.turno.los.dto.AgentDecisionRequest;
import com.turno.los.service.AgentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/agents")
@Tag(name = "Agent Management", description = "APIs for agent operations and decisions")
public class AgentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    
    private final AgentService agentService;
    
    @Autowired
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }
    
    /**
     * Process agent decision on a loan.
     * 
     * @param agentId The agent ID
     * @param loanId The loan ID
     * @param decisionRequest The agent's decision
     * @return Success response
     */
    @PutMapping("/{agentId}/loans/{loanId}/decision")
    @Operation(summary = "Process agent decision", 
               description = "Process an agent's decision on a loan application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Decision processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Loan or agent not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> processAgentDecision(
            @Parameter(description = "Agent ID") @PathVariable Long agentId,
            @Parameter(description = "Loan ID") @PathVariable String loanId,
            @Valid @RequestBody AgentDecisionRequest decisionRequest) {
        
        logger.info("Received agent decision request. Agent: {}, Loan: {}, Decision: {}", 
                   agentId, loanId, decisionRequest.getDecision());
        
        try {
            boolean success = agentService.processAgentDecision(agentId, loanId, decisionRequest);
            
            if (success) {
                Map<String, String> response = Map.of(
                    "message", "Decision processed successfully",
                    "loanId", loanId,
                    "agentId", agentId.toString(),
                    "decision", decisionRequest.getDecision().toString()
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Failed to process decision"));
            }
            
        } catch (Exception e) {
            logger.error("Error processing agent decision", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
    
    /**
     * Get agent by ID.
     * 
     * @param id The agent ID
     * @return The agent
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get agent by ID", 
               description = "Retrieve an agent by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agent found",
                    content = @Content(schema = @Schema(implementation = com.turno.los.entity.Agent.class))),
        @ApiResponse(responseCode = "404", description = "Agent not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<com.turno.los.entity.Agent> getAgentById(
            @Parameter(description = "Agent ID") @PathVariable Long id) {
        
        return agentService.getAgentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get agent by agent ID.
     * 
     * @param agentId The agent ID
     * @return The agent
     */
    @GetMapping("/by-agent-id/{agentId}")
    @Operation(summary = "Get agent by agent ID", 
               description = "Retrieve an agent by their agent ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agent found",
                    content = @Content(schema = @Schema(implementation = com.turno.los.entity.Agent.class))),
        @ApiResponse(responseCode = "404", description = "Agent not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<com.turno.los.entity.Agent> getAgentByAgentId(
            @Parameter(description = "Agent ID") @PathVariable String agentId) {
        
        return agentService.getAgentByAgentId(agentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all active agents.
     * 
     * @return List of active agents
     */
    @GetMapping
    @Operation(summary = "Get all active agents", 
               description = "Retrieve all active agents in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active agents retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<java.util.List<com.turno.los.entity.Agent>> getActiveAgents() {
        try {
            java.util.List<com.turno.los.entity.Agent> agents = agentService.getActiveAgents();
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            logger.error("Error retrieving active agents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create a new agent.
     * 
     * @param agentId The agent ID
     * @param name The agent name
     * @param email The agent email
     * @return The created agent
     */
    @PostMapping
    @Operation(summary = "Create new agent", 
               description = "Create a new agent in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Agent created successfully",
                    content = @Content(schema = @Schema(implementation = com.turno.los.entity.Agent.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Agent ID or email already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<com.turno.los.entity.Agent> createAgent(
            @Parameter(description = "Agent ID") @RequestParam String agentId,
            @Parameter(description = "Agent name") @RequestParam String name,
            @Parameter(description = "Agent email") @RequestParam String email) {
        
        try {
            com.turno.los.entity.Agent agent = agentService.createAgent(agentId, name, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(agent);
        } catch (Exception e) {
            logger.error("Error creating agent", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 