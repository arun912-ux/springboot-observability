package org.example.observability.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WeatherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String city;
    private String weather;
    private String temperature;
    private String wind;
    private String humidity;
    private String pressure;
    private String sunrise;
    private String sunset;
    private String timestamp;
    private String description;
    private String icon;


}
