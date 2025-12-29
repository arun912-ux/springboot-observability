package org.example.observability.client;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class WeatherClient {

    @Value("${weather.apikey}")
    private String apiKey;


    private final WebClient webClient;

    public WeatherClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openweathermap.org").build();
    }

    // https://api.openweathermap.org/data/2.5/weather?q=city&appid=0c3c357cbb2ceacfc2544131a21c4cdd&units=metric

    @WithSpan(value = "WeatherClient.getWeather")
    public String getWeather(String city) {
        Span.current().setAttribute("29-city", city);
        String response = webClient.get()
                .uri(url -> url
                        .path("/data/2.5/weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("response: {}", response);
        return response;
    }


}
