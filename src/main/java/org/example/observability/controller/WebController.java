package org.example.observability.controller;

import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import org.example.observability.repository.WeatherEntity;
import org.example.observability.service.WeatherService;
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
        log.info("Got request to get weather for city {}", city);

        Span.current().setAttribute("42-city", city);
        WeatherEntity weatherResponse = weatherService.getWeatherForCity(city);
        log.info("Weather response for city {} : {}", city, weatherResponse);
        return ResponseEntity.ok().body(weatherResponse);
    }

}
