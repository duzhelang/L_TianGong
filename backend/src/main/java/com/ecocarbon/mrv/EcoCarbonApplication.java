package com.ecocarbon.mrv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcoCarbonApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcoCarbonApplication.class, args);
    }
}
