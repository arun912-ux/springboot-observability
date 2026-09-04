package org.example.observability.controller;

import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.observability.repository.WeatherEntity;
import org.example.observability.service.WeatherService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class WebController {

    private final WeatherService weatherService;

    public WebController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "200 description", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - No Auth", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                    schema = @Schema(implementation = Object.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok().body("Hello World");
    }


    @Observed
    @GetMapping("home")
    public String home() throws InterruptedException {
        Thread.sleep(2000L);
        return "home";
    }

    @GetMapping("weather")
    public ResponseEntity<?> getWeather(@RequestParam(value = "city", required = true) String city) {
        log.debug("Got request to get weather for city {}", city);

        Span.current().setAttribute("42-city", city);
        WeatherEntity weatherResponse = weatherService.getWeatherForCity(city);
        log.info("Weather response for city {} : {}", city, weatherResponse);
        return ResponseEntity.ok().body(weatherResponse);
    }

}
