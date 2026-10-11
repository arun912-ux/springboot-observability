package org.example.observability.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

/**
 * Configuration for distributed tracing and observation context propagation across asynchronous execution threads.
 * <p>
 * Ensures that trace IDs, span IDs, and security/MDC attributes stored in thread-local storage
 * are properly copied when tasks are delegated to worker threads (e.g., Virtual Threads or ThreadPoolTaskExecutors).
 */
@Configuration(proxyBeanMethods = false)
public class ContextPropagationConfiguration {

    /**
     * Registers a {@link ContextPropagatingTaskDecorator} bean.
     * <p>
     * Wraps submitted asynchronous tasks so that thread-local snapshot values (Micrometer Observation,
     * OpenTelemetry Trace Context, and SecurityContext) are captured at submission time and restored
     * on the executing thread during task execution.
     *
     * @return a new {@link ContextPropagatingTaskDecorator} for task executors
     */
    @Bean
    ContextPropagatingTaskDecorator contextPropagatingTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

}