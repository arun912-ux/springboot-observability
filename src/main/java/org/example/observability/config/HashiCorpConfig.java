package org.example.observability.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration holder for database credentials resolved from HashiCorp Vault.
 * <p>
 * Injects dynamic database secrets (such as {@code db.username} and {@code db.password})
 * supplied by the HashiCorp Vault backend (via {@code spring-cloud-starter-vault-config})
 * and provides access to additional environment secrets for downstream configurations.
 */
@Slf4j
@Data
@Configuration
@AutoConfigureAfter(InfisicalSecretsProcessor.class)
public class HashiCorpConfig {

    private final String password;
    private final String username;
    private final Environment environment;

    /**
     * Constructs the {@link HashiCorpConfig} bean and logs retrieved property details.
     *
     * @param password    the database password resolved from Vault property source ({@code db.password})
     * @param username    the database username resolved from Vault property source ({@code db.username})
     * @param environment the Spring {@link Environment} to access additional runtime secrets
     */
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
