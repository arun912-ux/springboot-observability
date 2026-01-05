package org.example.observability.repository;

import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PGDBRepository extends JpaRepository<WeatherEntity, String> {

    @Observed(name = "PGDBRepository.save")
    WeatherEntity save(WeatherEntity entity);

    @Observed(name = "PGDBRepository.findByCity")
    Optional<List<WeatherEntity>> findByCityOrderByTimestampDesc(String city);
}
