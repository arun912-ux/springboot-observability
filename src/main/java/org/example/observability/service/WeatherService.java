package org.example.observability.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

import java.time.LocalDateTime;


@Slf4j
@Service
public class WeatherService {

    private final WeatherClient weatherClient;
    private final PGDBRepository pgDbRepository;

    public WeatherService(WeatherClient weatherClient, PGDBRepository pgDbRepository) {
        this.weatherClient = weatherClient;
        this.pgDbRepository = pgDbRepository;
    }

    @Timed(value = "weatherservice.getweather", histogram = true)
    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(value = "city-weather", key = "#city")
    @Observed(name = "weather-service.get-weather-for-city")
    public WeatherEntity getWeatherForCity(String city) {
        log.info("Getting weather for city {}", city);
        Span.current().setAttribute("24-city", city);
        String weather = weatherClient.getWeather(city);
        WeatherEntity weatherEntity = mapToEntity(weather, city);
        log.info("Weather for city {} : weather : {}", city, weatherEntity);
        pgDbRepository.save(weatherEntity);
        return weatherEntity;
    }


    private WeatherEntity mapToEntity(String weather, String city) {
        JsonElement jsonElement = JsonParser.parseString(weather);
        WeatherEntity weatherEntity = new WeatherEntity();
        weatherEntity.setCity(city);
        weatherEntity.setIcon(jsonElement.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString());
        weatherEntity.setWeather(jsonElement.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString());
        weatherEntity.setTemperature(jsonElement.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsString());
        weatherEntity.setHumidity(jsonElement.getAsJsonObject().get("main").getAsJsonObject().get("humidity").getAsString());
        weatherEntity.setPressure(jsonElement.getAsJsonObject().get("main").getAsJsonObject().get("pressure").getAsString());
        weatherEntity.setWind(jsonElement.getAsJsonObject().get("wind").getAsJsonObject().get("speed").getAsString());
        long epoch = jsonElement.getAsJsonObject().get("dt").getAsLong();
        weatherEntity.setTimestamp(String.valueOf(LocalDateTime.ofEpochSecond(epoch, 0, java.time.ZoneOffset.UTC)));
        weatherEntity.setSunrise(jsonElement.getAsJsonObject().get("sys").getAsJsonObject().get("sunrise").getAsString());
        weatherEntity.setSunset(jsonElement.getAsJsonObject().get("sys").getAsJsonObject().get("sunset").getAsString());
        weatherEntity.setDescription(jsonElement.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString());
        return weatherEntity;
    }


}
