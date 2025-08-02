package com.turno.los.config;

import com.turno.los.entity.Agent;
import com.turno.los.repository.AgentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final AgentRepository agentRepository;
    
    @Autowired
    public DataInitializer(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing sample data...");
        
        if (agentRepository.count() == 0) {
            createSampleAgents();
        }
        
        logger.info("Sample data initialization completed.");
    }
    
    private void createSampleAgents() {
        logger.info("Creating sample agents...");
        
        Agent manager1 = new Agent("AGENT-001", "John Manager", "john.manager@turno.com");
        manager1.setPhone("+1234567890");
        manager1.setMaxLoanAmount(500000.0);
        manager1.setSpecializations("HOME,BUSINESS");
        agentRepository.save(manager1);
        
        Agent manager2 = new Agent("AGENT-002", "Sarah Manager", "sarah.manager@turno.com");
        manager2.setPhone("+1234567891");
        manager2.setMaxLoanAmount(300000.0);
        manager2.setSpecializations("PERSONAL,AUTO");
        agentRepository.save(manager2);
        
        Agent agent1 = new Agent("AGENT-003", "Mike Agent", "mike.agent@turno.com");
        agent1.setPhone("+1234567892");
        agent1.setMaxLoanAmount(100000.0);
        agent1.setSpecializations("PERSONAL");
        agent1.setManager(manager1);
        agentRepository.save(agent1);
        
        Agent agent2 = new Agent("AGENT-004", "Lisa Agent", "lisa.agent@turno.com");
        agent2.setPhone("+1234567893");
        agent2.setMaxLoanAmount(150000.0);
        agent2.setSpecializations("AUTO");
        agent2.setManager(manager1);
        agentRepository.save(agent2);
        
        Agent agent3 = new Agent("AGENT-005", "David Agent", "david.agent@turno.com");
        agent3.setPhone("+1234567894");
        agent3.setMaxLoanAmount(200000.0);
        agent3.setSpecializations("HOME");
        agent3.setManager(manager2);
        agentRepository.save(agent3);
        
        Agent agent4 = new Agent("AGENT-006", "Emma Agent", "emma.agent@turno.com");
        agent4.setPhone("+1234567895");
        agent4.setMaxLoanAmount(75000.0);
        agent4.setSpecializations("PERSONAL,AUTO");
        agent4.setManager(manager2);
        agentRepository.save(agent4);
        
        logger.info("Created {} sample agents", agentRepository.count());
    }
} 