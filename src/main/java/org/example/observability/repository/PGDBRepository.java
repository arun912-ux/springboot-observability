package org.example.observability.repository;

import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PGDBRepository extends JpaRepository<WeatherEntity, String> {

    @Observed(name = "PGDBRepository.save")
    @NonNull
    WeatherEntity save(@NonNull WeatherEntity entity);

    @Observed(name = "PGDBRepository.findByCity")
    List<WeatherEntity> findByCityOrderByTimestampDesc(String city);
}
