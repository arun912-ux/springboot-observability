package org.example.observability.service;

import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.example.observability.client.WeatherClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @Cacheable(value = "city-weather", key = "#city")
//    @Observed(name = "weather-service.get-weather-for-city")
    public String getWeatherForCity(String city) {
        log.info("Getting weather for city {}", city);
        String weather = weatherClient.getWeather(city);
        log.info("Weather for city {}", city);
        return weather;
    }


}
