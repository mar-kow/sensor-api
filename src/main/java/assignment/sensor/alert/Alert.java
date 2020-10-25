package assignment.sensor.alert;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Alert {

    private final ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private final List<Integer> measurements;

}
