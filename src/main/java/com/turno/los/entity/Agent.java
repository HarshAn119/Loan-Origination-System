package com.turno.los.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "agents", indexes = {
    @Index(name = "idx_agent_email", columnList = "email"),
    @Index(name = "idx_agent_manager_id", columnList = "manager_id"),
    @Index(name = "idx_agent_status", columnList = "status")
})
public class Agent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "agent_id", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Agent ID is required")
    @Size(max = 50, message = "Agent ID must not exceed 50 characters")
    private String agentId;
    
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Agent name is required")
    @Size(max = 100, message = "Agent name must not exceed 100 characters")
    private String name;
    
    @Column(name = "email", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Column(name = "phone", length = 20)
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Agent status is required")
    private AgentStatus status = AgentStatus.ACTIVE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Agent manager;
    
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Agent> subordinates = new ArrayList<>();
    
    @Column(name = "max_loan_amount")
    @DecimalMin(value = "0.01", message = "Max loan amount must be greater than 0")
    private Double maxLoanAmount;
    
    @Column(name = "specializations", length = 500)
    @Size(max = 500, message = "Specializations must not exceed 500 characters")
    private String specializations;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public Agent() {}
    
    public Agent(String agentId, String name, String email) {
        this.agentId = agentId;
        this.name = name;
        this.email = email;
        this.status = AgentStatus.ACTIVE;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public AgentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AgentStatus status) {
        this.status = status;
    }
    
    public Agent getManager() {
        return manager;
    }
    
    public void setManager(Agent manager) {
        this.manager = manager;
    }
    
    public List<Agent> getSubordinates() {
        return subordinates;
    }
    
    public void setSubordinates(List<Agent> subordinates) {
        this.subordinates = subordinates;
    }
    
    public Double getMaxLoanAmount() {
        return maxLoanAmount;
    }
    
    public void setMaxLoanAmount(Double maxLoanAmount) {
        this.maxLoanAmount = maxLoanAmount;
    }
    
    public String getSpecializations() {
        return specializations;
    }
    
    public void setSpecializations(String specializations) {
        this.specializations = specializations;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return Objects.equals(id, agent.id) && Objects.equals(agentId, agent.agentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, agentId);
    }
    
    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", agentId='" + agentId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
    
    public enum AgentStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUSPENDED("Suspended");
        
        private final String displayName;
        
        AgentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}