package org.example.observability.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {


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
