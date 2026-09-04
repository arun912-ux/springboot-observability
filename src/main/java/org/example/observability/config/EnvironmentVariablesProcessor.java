package org.example.observability.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class EnvironmentVariablesProcessor implements EnvironmentPostProcessor {
    /**
     * Post-process the given {@code environment}.
     *
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     */

    final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
    final JsonMapper jsonMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();
    final YAMLMapper yamlMapper = YAMLMapper.builder()
            .enable(YAMLParser.Feature.EMPTY_STRING_AS_NULL)
            .build();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new ClassPathResource("observability.yaml");
        List<PropertySource<?>> propertySources = null;

        try {
            propertySources = yamlLoader.load("observability.yaml", resource);
        } catch (IOException e) {
            log.error("Observability isn't loaded due to parsing error in observability.yaml", e);
            throw new RuntimeException(e);
        }

        propertySources.forEach(ps -> {
            environment.getPropertySources().addLast(ps);
        });


    }
}
