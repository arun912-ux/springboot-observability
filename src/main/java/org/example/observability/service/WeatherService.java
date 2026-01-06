package org.example.observability.service;


import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import org.example.observability.client.WeatherClient;
import org.example.observability.repository.PGDBRepository;
import org.example.observability.repository.WeatherEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;


@Slf4j
@Service
public class WeatherService {

    private final WeatherClient weatherClient;
    private final PGDBRepository pgDbRepository;
    private final ObjectMapper mapper;

    public WeatherService(WeatherClient weatherClient, PGDBRepository pgDbRepository, ObjectMapper mapper) {
        this.weatherClient = weatherClient;
        this.pgDbRepository = pgDbRepository;
        this.mapper = mapper;
    }

    @Timed(value = "weatherservice.getweather", histogram = true)
    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(value = "city-weather", key = "#city")
    @Observed(name = "weather-service.get-weather-for-city")
    public WeatherEntity getWeatherForCity(String city) {
        log.info("Getting weather for city {}", city);
        Span.current().setAttribute("24-city", city);
        WeatherEntity weather1 = pgDbRepository.findByCityOrderByTimestampDesc(city).orElseThrow().get(0);
        String weather;
        WeatherEntity weatherEntity;
        if (weather1 == null) {
            weather = weatherClient.getWeather(city);
            weatherEntity = mapToEntity(weather, city);
            pgDbRepository.save(weatherEntity);
        } else {
            weatherEntity = weather1;
        }
        log.info("Weather for city {} : weather : {}", city, weatherEntity);
        return weatherEntity;
    }


    private WeatherEntity mapToEntity(String weather, String city) {
        WeatherEntity weatherEntity = new WeatherEntity();
        JsonNode jsonNode = mapper.readTree(weather);
        weatherEntity.setCity(city);
        weatherEntity.setIcon(jsonNode.get("weather").get(0).get("icon").asString());
        weatherEntity.setWeather(jsonNode.get("weather").get(0).get("main").asString());
        weatherEntity.setTemperature(jsonNode.get("main").get("temp").asString());
        weatherEntity.setHumidity(jsonNode.get("main").get("humidity").asString());
        weatherEntity.setPressure(jsonNode.get("main").get("pressure").asString());
        weatherEntity.setWind(jsonNode.get("wind").get("speed").asString());
        long epoch = jsonNode.get("dt").asLong();
        weatherEntity.setTimestamp(String.valueOf(LocalDateTime.ofEpochSecond(epoch, 0, java.time.ZoneOffset.UTC)));
        weatherEntity.setSunrise(jsonNode.get("sys").get("sunrise").asString());
        weatherEntity.setSunset(jsonNode.get("sys").get("sunset").asString());
        weatherEntity.setDescription(jsonNode.get("weather").get(0).get("description").asString());
        return weatherEntity;
    }


}
