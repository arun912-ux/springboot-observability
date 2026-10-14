package org.example.observability.repository;

public interface WeatherProjection {


    String getId();
    String getCity();
    String getWeather();
    String getTemperature();
    String getIcon();

    default String customToString() {
        return "WeatherProjection {" +
                "id: '" + getId() + '\'' +
                ", city: '" + getCity() + '\'' +
                ", weather: '" + getWeather() + '\'' +
                ", temperature: '" + getTemperature() + '\'' +
                ", icon: '" + getIcon() + '\'' +
                '}';
    }
}
