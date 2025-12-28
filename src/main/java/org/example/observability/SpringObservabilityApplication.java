package org.example.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@EnableCaching
@SpringBootApplication
public class SpringObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringObservabilityApplication.class, args);
    }

}
