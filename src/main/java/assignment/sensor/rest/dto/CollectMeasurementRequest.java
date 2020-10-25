package assignment.sensor.rest.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CollectMeasurementRequest {
    private Integer co2;
    private ZonedDateTime time;

}
