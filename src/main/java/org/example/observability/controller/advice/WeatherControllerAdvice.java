package org.example.observability.controller.advice;


import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WeatherControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    @Observed(name = "WeatherControllerAdvice.handleEntityNotFoundException")
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

}
