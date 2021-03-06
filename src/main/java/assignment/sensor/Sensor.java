package assignment.sensor;

import assignment.sensor.alert.AlertListener;
import assignment.sensor.metrics.CalculatedSensorMetrics;
import assignment.sensor.metrics.RollingWindowSensorMetrics;
import assignment.sensor.metrics.SensorMetrics;
import assignment.sensor.alert.Alert;
import assignment.sensor.status.SensorStatusPolicy;
import assignment.sensor.status.SensorStatus;
import com.google.common.annotations.VisibleForTesting;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sensor {
    private final List<Alert> alerts = new ArrayList<>();
    private final AlertListener alertListener = new SensorAlertListener();
    private final SensorStatusPolicy sensorStatusPolicy;
    private final RollingWindowSensorMetrics sensorMetrics;

    public Sensor() {
        this.sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        this.sensorMetrics = new RollingWindowSensorMetrics(30);
    }

    @VisibleForTesting
    Sensor(SensorStatusPolicy sensorStatusPolicy, RollingWindowSensorMetrics sensorMetrics) {
        this.sensorStatusPolicy = sensorStatusPolicy;
        this.sensorMetrics = sensorMetrics;
    }

    public synchronized void addMeasurement(ZonedDateTime time, Integer measurement) {
        sensorStatusPolicy.addMeasurement(time, measurement);
        sensorMetrics.addMeasurement(time, measurement);
    }

    public SensorStatus status() {
        return sensorStatusPolicy.status();
    }

    public SensorMetrics metrics() {
        int maxMeasurement = sensorMetrics.maxMeasurement();
        int averageMeasurement = sensorMetrics.averageMeasurement();
        return new CalculatedSensorMetrics(maxMeasurement, averageMeasurement);
    }

    public List<Alert> alerts() {
        return Collections.unmodifiableList(alerts);
    }

    class SensorAlertListener implements AlertListener {

        @Override
        public void onNewAlertCreated(ZonedDateTime startTime, List<Integer> measurements) {
            alerts.add(new Alert(startTime, measurements));
        }

        @Override
        public void onAlertClosed(ZonedDateTime endTime) {
            var lastAlert = alerts.get(alerts.size() - 1);
            if (lastAlert.getEndTime() != null) {
                throw new IllegalStateException("Trying to close already closed alert");
            }
            lastAlert.setEndTime(endTime);
        }

    }

    @VisibleForTesting
    AlertListener alertListener() {
        return alertListener;
    }

}
