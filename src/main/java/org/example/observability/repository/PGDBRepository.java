package org.example.observability.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PGDBRepository extends JpaRepository<WeatherEntity, String> {

    @Observed(name = "PGDBRepository.save")
    WeatherEntity save(WeatherEntity entity);
}
