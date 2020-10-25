package assignment.sensor.alert;

import java.time.ZonedDateTime;
import java.util.List;

public interface AlertListener {

    void onNewAlertCreated(ZonedDateTime startTime, List<Integer> measurements);

    void onAlertClosed(ZonedDateTime endTime);

}
