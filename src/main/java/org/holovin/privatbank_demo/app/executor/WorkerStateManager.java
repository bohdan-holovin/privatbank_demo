package org.holovin.privatbank_demo.app.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class WorkerStateManager {
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    public boolean isShuttingDown() {
        return isShuttingDown.get();
    }

    public void initiateShutdown() {
        log.info("Shutdown initiated");
        isShuttingDown.set(true);
    }

    public boolean shouldContinueProcessing() {
        return !isShuttingDown() && !Thread.currentThread().isInterrupted();
    }
}
