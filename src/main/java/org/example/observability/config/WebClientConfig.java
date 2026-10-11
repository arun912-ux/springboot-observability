package org.example.observability.config;

import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;

/**
 * Configuration for reactive and outbound {@link org.springframework.web.reactive.function.client.WebClient} instances.
 * <p>
 * Provides customizers that inject Spring Security OAuth2 M2M (Machine-to-Machine)
 * token exchange filters so that outbound HTTP requests automatically attach valid Bearer tokens.
 */
@Configuration
public class WebClientConfig {

    /**
     * This is not working since spring oauth2 client does not support audience claim in the token request.
     * Auth0 server is rejecting the token request since audience claim is missing in the token request.
     *
     * So, we are not using this bean and using the default WebClient without any filter.
     */


    /**
     * Configures a {@link WebClientCustomizer} that applies an OAuth2 exchange filter to all auto-configured
     * {@link org.springframework.web.reactive.function.client.WebClient.Builder} instances.
     * <p>
     * Associates the {@link ServletOAuth2AuthorizedClientExchangeFilterFunction} with the default
     * client registration {@code "downstream-m2m"} configured in {@code application.yml},
     * ensuring authorized token propagation during outbound service-to-service communication.
     *
     * @param oAuth2AuthorizedClientManager the Spring Security OAuth2 authorized client manager
     * @return a {@link WebClientCustomizer} that registers the OAuth2 filter with WebClient builders
     */


//    @Bean
//    public WebClientCustomizer webClient(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
//        return webClientBuilder -> {
//            ServletOAuth2AuthorizedClientExchangeFilterFunction filterFunction =
//                    new ServletOAuth2AuthorizedClientExchangeFilterFunction(oAuth2AuthorizedClientManager);
//
//            filterFunction.setDefaultClientRegistrationId("downstream-m2m");        // this should match the client registration id in your application.yml
////            filterFunction.setDefaultOAuth2AuthorizedClient(true);                  // Automatically authorize requests with the default client
//            webClientBuilder.filter(filterFunction);
//
//        };
//    }

}
