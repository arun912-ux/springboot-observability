package org.example.observability.dto;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "weather_data")
public class WeatherMongo {

    @Id
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

    @Version
    private Long version;

}
