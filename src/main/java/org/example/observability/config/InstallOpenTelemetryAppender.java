package org.example.observability.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for installing the OpenTelemetryAppender to enable OpenTelemetry logging.
 * This is to expose the OpenTelemetryAppender to the logging framework so that it can be used to send logs to OpenTelemetry.
 * The OpenTelemetryAppender is a logback appender that sends logs to OpenTelemetry.
 *
 * This is required by logback-spring.xml to enable OpenTelemetry logging.
 */

@Slf4j
@Component
class InstallOpenTelemetryAppender implements InitializingBean {

    private final OpenTelemetry openTelemetry;

    /**
     * Constructs the appender installer with the auto-configured {@link OpenTelemetry} instance.
     *
     * @param openTelemetry the OpenTelemetry SDK instance containing active tracer, meter, and logger providers
     */
    InstallOpenTelemetryAppender(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    /**
     * Invoked by the Spring container after all bean properties have been set.
     * Hooks the auto-configured {@link OpenTelemetry} instance into the Logback
     * {@link OpenTelemetryAppender} so that all application log events capture
     * active trace IDs and span IDs and are forwarded via OTLP.
     */
    @Override
    public void afterPropertiesSet() {
        log.info("OpenTelemetryAppender initialized : {}", openTelemetry);
        OpenTelemetryAppender.install(this.openTelemetry);
    }

}
