package com.project.sleep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching // 캐싱 기능
@SpringBootApplication
@ConfigurationPropertiesScan
public class SleepServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SleepServiceApplication.class, args);
    }

}
