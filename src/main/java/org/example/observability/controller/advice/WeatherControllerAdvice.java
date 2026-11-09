package org.example.observability.controller.advice;


import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WeatherControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    @Observed(name = "WeatherControllerAdvice.handleEntityNotFoundException")
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Observed(name = "WeatherControllerAdvice.handleMethodArgumentNotValidException")
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            System.out.println("Field error: " + error.getField() + " - " + error.getDefaultMessage());
        });
        return ResponseEntity.status(400).body(ex.getMessage());

    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail problemDetail(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
    }

}
