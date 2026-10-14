package org.example.observability.repository;

import io.micrometer.observation.annotation.Observed;
import org.example.observability.dto.WeatherEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PGDBRepository extends JpaRepository<WeatherEntity, String> {

    @Observed(name = "PGDBRepository.save")
    @NonNull
    WeatherEntity save(@NonNull WeatherEntity entity);


    @Query(name = "findCityWeatherByCity", nativeQuery = true)
//    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Observed(name = "PGDBRepository.findByCity")
    List<WeatherEntity> findByCityOrderByTimestampDesc(String city);

    @Query(name = "findWeatherProjectionByCity", nativeQuery = true)
    List<WeatherProjection> findByCity(String city);
}

