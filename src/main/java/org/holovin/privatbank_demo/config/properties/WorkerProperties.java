package org.holovin.privatbank_demo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.worker")
@Data
public class WorkerProperties {
    private int workerCount = 5;
    private int batchSize = 10;
    private int pollingDelayMs = 1000;
    private int emptyBatchDelayMs = 2000;
    private int shutdownTimeoutSeconds = 30;
}
