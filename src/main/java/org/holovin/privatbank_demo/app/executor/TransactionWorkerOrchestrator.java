package org.holovin.privatbank_demo.app.executor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.config.properties.WorkerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionWorkerOrchestrator {

    private final ApplicationContext applicationContext;
    private final WorkerProperties config;
    private final WorkerStateManager stateManager;

    @PostConstruct
    public void startWorkers() {
        IntStream.rangeClosed(1, config.getWorkerCount())
                .forEach(this::startWorker);

        log.info("Started {} async transaction workers using prototype scope", config.getWorkerCount());
    }

    private void startWorker(int workerId) {
        var workerTask = applicationContext.getBean(TransactionWorkerTask.class);
        workerTask.setWorkerId(workerId);
        workerTask.startProcessing();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down transaction workers...");
        stateManager.initiateShutdown();
    }
}
