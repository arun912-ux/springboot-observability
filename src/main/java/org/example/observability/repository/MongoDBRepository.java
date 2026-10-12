package org.example.observability.repository;

import io.micrometer.observation.annotation.Observed;
import org.example.observability.dto.WeatherMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDBRepository extends MongoRepository<WeatherMongo, String> {

    @Observed(name = "MongoDBRepository.save")
    WeatherMongo save(WeatherMongo entity);
}

