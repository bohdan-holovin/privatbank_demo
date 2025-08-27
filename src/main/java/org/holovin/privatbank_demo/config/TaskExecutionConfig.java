package org.holovin.privatbank_demo.config;

import org.holovin.privatbank_demo.config.properties.WorkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class TaskExecutionConfig {

    @Bean("transactionWorkerExecutor")
    public TaskExecutor taskExecutor(WorkerProperties config) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getWorkerCount());
        executor.setMaxPoolSize(config.getWorkerCount());
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("TransactionWorker-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(config.getShutdownTimeoutSeconds());
        executor.initialize();
        return executor;
    }
}
