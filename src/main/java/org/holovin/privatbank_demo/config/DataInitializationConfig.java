package org.holovin.privatbank_demo.config;

import lombok.extern.slf4j.Slf4j;
import org.holovin.privatbank_demo.app.service.devonly.DevOnlyDataInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DataInitializationConfig {

    @Bean
    public ApplicationRunner dataInitializer(
            @Autowired(required = false) DevOnlyDataInitService devOnlyDataInitializer
    ) {
        return args -> {
            if (devOnlyDataInitializer != null) {
                log.info("Initializing additional test/dev data");
                devOnlyDataInitializer.populateTestData(10, 2, 5);
            }
        };
    }
}
