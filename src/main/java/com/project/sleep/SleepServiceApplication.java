package com.project.sleep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class SleepServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SleepServiceApplication.class, args);
    }

}
