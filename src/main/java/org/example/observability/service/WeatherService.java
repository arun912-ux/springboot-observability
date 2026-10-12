package org.example.observability.service;


import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.example.observability.client.WeatherClient;
import org.example.observability.dto.WeatherEntity;
import org.example.observability.dto.WeatherMongo;
import org.example.observability.repository.MongoDBRepository;
import org.example.observability.repository.PGDBRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class WeatherService {

    private final WeatherClient weatherClient;
    private final PGDBRepository pgDbRepository;
    private final ObjectMapper mapper;

    private final MongoDBRepository mongoDBRepository;

    public WeatherService(WeatherClient weatherClient, PGDBRepository pgDbRepository, ObjectMapper mapper, MongoDBRepository mongoDBRepository) {
        this.weatherClient = weatherClient;
        this.pgDbRepository = pgDbRepository;
        this.mongoDBRepository = mongoDBRepository;
        this.mapper = mapper;
    }

    @Timed(value = "weatherservice.getweather", histogram = true)
    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(value = "city-weather", key = "#city", cacheManager = "cacheManager")
    @Observed(name = "weather-service.get-weather-for-city")
    public WeatherEntity getWeatherForCity(String city) {
        log.info("Getting weather for city {}", city);
        Span.current().setAttribute("24-city", city);
        List<WeatherEntity> weather1 = pgDbRepository.findByCityOrderByTimestampDesc(city);
        String weather;
        WeatherEntity weatherEntity;
        if (weather1 == null || weather1.isEmpty()) {
            weather = weatherClient.getWeather(city);
            log.info(">> weather: {}", weather);
            weatherEntity = mapToEntity(weather, city);
            WeatherEntity saved = pgDbRepository.save(weatherEntity);
            log.info("Weather for city {} : weather : {}", city, saved);
            WeatherMongo savedMongo = mongoDBRepository.save(mapEntityToMongo(saved));
            log.info("Weather for city {} : weatherMongo : {}", city, savedMongo);

        } else {
            weatherEntity = weather1.get(0);
            log.info("Weather for city {} : weather : {}", city, weatherEntity);
        }

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        executorService.submit(() -> {
            String s = "return" + " - ";
            s += 1;
            return s;
        });
        executorService.execute(() -> {
        });
        executorService.close();

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        fixedThreadPool.submit(() -> {
            String s = "return" + " - ";
            s += 1;
            return s;
        });
        fixedThreadPool.submit(() -> {
        });
        fixedThreadPool.execute(() -> {
        });
        fixedThreadPool.close();
        return weatherEntity;
    }


    private WeatherEntity mapToEntity(String weather, String city) {
        if (Strings.CI.startsWith(weather, "404")) {
            throw new EntityNotFoundException("Weather not found for city " + city);
        }
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

    private WeatherMongo mapEntityToMongo(WeatherEntity weatherEntity) {
        WeatherMongo weatherMongo = new WeatherMongo();
        weatherMongo.setCity(weatherEntity.getCity());
        weatherMongo.setIcon(weatherEntity.getIcon());
        weatherMongo.setWeather(weatherEntity.getWeather());
        weatherMongo.setTemperature(weatherEntity.getTemperature());
        weatherMongo.setHumidity(weatherEntity.getHumidity());
        weatherMongo.setPressure(weatherEntity.getPressure());
        weatherMongo.setWind(weatherEntity.getWind());
        weatherMongo.setTimestamp(weatherEntity.getTimestamp());
        weatherMongo.setSunrise(weatherEntity.getSunrise());
        weatherMongo.setSunset(weatherEntity.getSunset());
        weatherMongo.setDescription(weatherEntity.getDescription());
        return weatherMongo;
    }

}
