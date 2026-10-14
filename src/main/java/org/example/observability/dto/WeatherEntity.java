package org.example.observability.dto;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SqlResultSetMapping(
        name = "WeatherEntityMapping",
        classes = @ConstructorResult(
                targetClass = WeatherEntity.class,
                columns = {
                        @ColumnResult(name = "id", type = String.class),
                        @ColumnResult(name = "city", type = String.class),
                        @ColumnResult(name = "weather", type = String.class),
                        @ColumnResult(name = "temperature", type = String.class),
                        @ColumnResult(name = "wind", type = String.class),
                        @ColumnResult(name = "humidity", type = String.class),
                        @ColumnResult(name = "pressure", type = String.class),
                        @ColumnResult(name = "sunrise", type = String.class),
                        @ColumnResult(name = "sunset", type = String.class),
                        @ColumnResult(name = "timestamp", type = String.class),
                        @ColumnResult(name = "description", type = String.class),
                        @ColumnResult(name = "icon", type = String.class)
                }
        )
)
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "findCityWeatherByCity",
                query = "SELECT * FROM weather_entity WHERE city = :city",
                resultSetMapping = "WeatherEntityMapping"
        ),
        @NamedNativeQuery(
                name = "findWeatherProjectionByCity",
                query = "SELECT id, city, weather, temperature, icon FROM weather_entity WHERE city = :city"
        )
})

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

    @Version
    private Long version;

}
