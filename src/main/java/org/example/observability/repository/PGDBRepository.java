package org.example.observability.repository;

import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.LockModeType;
import org.example.observability.dto.WeatherEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface PGDBRepository extends JpaRepository<WeatherEntity, String> {

    @Observed(name = "PGDBRepository.save")
    @NonNull
    WeatherEntity save(@NonNull WeatherEntity entity);


    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Observed(name = "PGDBRepository.findByCity")
    List<WeatherEntity> findByCityOrderByTimestampDesc(String city);
}
