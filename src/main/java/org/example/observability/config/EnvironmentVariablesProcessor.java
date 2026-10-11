package org.example.observability.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

/**
 * {@link EnvironmentPostProcessor} responsible for loading custom observability YAML configurations
 * into the Spring environment during pre-context initialization.
 * <p>
 * Uses {@link YamlPropertySourceLoader} to parse {@code observability.yaml} from the classpath
 * and appends the resulting {@link PropertySource} items to the {@link ConfigurableEnvironment}.
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvironmentVariablesProcessor implements EnvironmentPostProcessor {

    final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
    final JsonMapper jsonMapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();
    final YAMLMapper yamlMapper = YAMLMapper.builder()
            .enable(YAMLParser.Feature.EMPTY_STRING_AS_NULL)
            .build();

    /**
     * Post-processes the given {@code environment} by loading and registering {@code observability.yaml}.
     *
     * @param environment the {@link ConfigurableEnvironment} to which the custom property source is appended
     * @param application the {@link SpringApplication} being bootstrapped
     * @throws RuntimeException if {@code observability.yaml} fails to load or parse
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new ClassPathResource("observability.yaml");
        List<PropertySource<?>> propertySources = null;

        System.out.println("Inside EnvironmentVariablesProcessor");

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
