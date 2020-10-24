package assignment.sensor.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class AlertDto {
    private final ZonedDateTime startDateTime;
    private final ZonedDateTime endDateTime;
    private final int measurement1;
    private final int measurement2;
    private final int measurement3;
}
