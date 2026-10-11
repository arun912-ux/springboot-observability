package org.example.observability.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration class for customized relational {@link DataSource} setup.
 * <p>
 * Injects {@link HashiCorpConfig} for potential dynamic database credential injection
 * (e.g., retrieving rotated database usernames and passwords from Vault) when
 * overriding the default Spring Boot DataSource auto-configuration.
 */
@Configuration
public class DatasourceConfig {

    private HashiCorpConfig hashiCorpConfig;

    /**
     * Constructs the DataSource configuration with HashiCorp Vault credential integration.
     *
     * @param hashiCorpConfig the configuration bean providing dynamic database credentials from Vault
     */
    public DatasourceConfig(HashiCorpConfig hashiCorpConfig) {
        this.hashiCorpConfig = hashiCorpConfig;
    }

//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//        return DataSourceBuilder.create()
//                .url(dataSourceProperties.getUrl())
//                .username(dataSourceProperties.getUsername())
//                .password(dataSourceProperties.getPassword())
//                .driverClassName(dataSourceProperties.getDriverClassName())
//                .build();
//    }

}
