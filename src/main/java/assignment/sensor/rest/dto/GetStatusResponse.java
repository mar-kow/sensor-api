package assignment.sensor.rest.dto;

import lombok.Data;

@Data
public class GetStatusResponse {
    private final Status status;

    public enum Status {
        OK,
        WARN,
        ALERT
    }
}
