package assignment.sensor.rest;

import assignment.sensor.SensorNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class SensorControllerAdvice {

    @ExceptionHandler(value = SensorNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Sensor Not Found")
    protected void handle(SensorNotFoundException exc) {
        log.warn(exc.getMessage());
    }

}
