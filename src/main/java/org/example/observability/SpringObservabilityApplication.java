package org.example.observability;

import org.example.observability.controller.WebController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SpringObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringObservabilityApplication.class, args);
    }


    // kafka listener -> db
    // web -> kafka producer
    // web -> web -> db

}
