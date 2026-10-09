package org.example.observability.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Data
@Configuration
@AutoConfigureAfter(InfisicalSecretsProcessor.class)
public class HashiCorpConfig {

    private final String password;
    private final String username;
    private final Environment environment;

    public HashiCorpConfig(@Value("${db.password}") String password,
                           @Value("${db.username}") String username,
                           Environment environment) {
        this.password = password;
        this.username = username;
        this.environment = environment;

        log.info("HashiCorp username: {}", this.username);
        log.info("HashiCorp password: {}", this.password);
        log.info("Infisical secrets TWILIO_AUTH_TOKEN: {}", environment.getProperty("TWILIO_AUTH_TOKEN"));
    }
}
