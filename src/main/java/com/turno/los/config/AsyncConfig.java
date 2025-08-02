package com.turno.los.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Value("${los.processing.thread-pool-size:5}")
    private int threadPoolSize;
    
    @Value("${los.processing.queue-capacity:100}")
    private int queueCapacity;
    
    /**
     * Configure the thread pool executor for loan processing.
     * This executor will handle background loan processing tasks.
     * 
     * @return ThreadPoolTaskExecutor configured for loan processing
     */
    @Bean(name = "loanProcessingExecutor")
    public Executor loanProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(threadPoolSize);
        
        executor.setMaxPoolSize(threadPoolSize * 2);
        
        executor.setQueueCapacity(queueCapacity);
        
        executor.setThreadNamePrefix("LoanProcessor-");
        
        executor.setAllowCoreThreadTimeOut(true);
        
        executor.setKeepAliveSeconds(60);
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        
        return executor;
    }
    
    /**
     * Configure a separate thread pool for notification processing.
     * This ensures notifications don't block loan processing.
     * 
     * @return ThreadPoolTaskExecutor configured for notifications
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Notification-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(30);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        
        executor.initialize();
        
        return executor;
    }
} 