package org.example.observability.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI 3.0 specification and Swagger UI documentation.
 * <p>
 * Customizes the API metadata (title, version, description) and registers
 * an OAuth2 Authorization Code security scheme so that protected endpoints
 * can be tested interactively through the Swagger UI.
 */
@Configuration
public class CustomOpenAPIConfig {

    /**
     * Configures and provides the primary {@link OpenAPI} specification bean.
     * <p>
     * Defines:
     * <ul>
     *   <li>API Metadata: Service title, version, summary, and description.</li>
     *   <li>Security Scheme: OAuth 2.0 authorization code flow configuration with required scopes.</li>
     *   <li>Global Security Requirement: Enforces the "oauth2" security requirement across documented endpoints.</li>
     * </ul>
     *
     * @return the configured {@link OpenAPI} instance for SpringDoc to serve at {@code /api-docs}
     */
    @Bean
    public OpenAPI openAPIConfig() {

        OAuthFlow flow = new OAuthFlow()
                .authorizationUrl("https://example.com")
                .tokenUrl("https://example.com")
                .scopes(new Scopes().addString("read", "Read"));

        OAuthFlows flows = new OAuthFlows().authorizationCode(flow);

        return new OpenAPI()
                .info(new Info()
                        .title("Observability API")
                        .version("1.0")
                        .description("This is the observability API")
                        .summary("Observability API")
                )
                .addSecurityItem(new SecurityRequirement().addList("oauth2"))
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .flows(flows)
                                .name("oauth2")
                                .type(SecurityScheme.Type.OAUTH2)
                        )
                );
    }

}
