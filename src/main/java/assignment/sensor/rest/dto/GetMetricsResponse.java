package assignment.sensor.rest.dto;

import lombok.Data;

@Data
public class GetMetricsResponse {
    private final int maxLast30Days;
    private final int avgLast30Days;

}
