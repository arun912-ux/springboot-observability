package org.example.observability.config;

import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClientCustomizer webClient(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        return webClientBuilder -> {
            ServletOAuth2AuthorizedClientExchangeFilterFunction filterFunction =
                    new ServletOAuth2AuthorizedClientExchangeFilterFunction(oAuth2AuthorizedClientManager);
            filterFunction.setDefaultClientRegistrationId("client-registration-name");
            webClientBuilder.filter(filterFunction);

        };
    }

}
