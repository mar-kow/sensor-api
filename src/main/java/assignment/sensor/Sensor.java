package assignment.sensor;

import assignment.sensor.alert.AlertListener;
import assignment.sensor.metrics.CalculatedSensorMetrics;
import assignment.sensor.metrics.RunningWindowSensorMetrics;
import assignment.sensor.metrics.SensorMetrics;
import assignment.sensor.alert.Alert;
import assignment.sensor.state.SensorState;
import assignment.sensor.state.SensorStatus;
import com.google.common.annotations.VisibleForTesting;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sensor {
    private final SensorState sensorState;
    private final RunningWindowSensorMetrics sensorMetrics;
    private final List<Alert> alerts = new ArrayList<>();

    public Sensor() {
        this.sensorState = new SensorState(new SensorAlertListener());
        this.sensorMetrics = new RunningWindowSensorMetrics(30);
    }

    @VisibleForTesting
    Sensor(SensorState sensorState, RunningWindowSensorMetrics sensorMetrics) {
        this.sensorState = sensorState;
        this.sensorMetrics = sensorMetrics;
    }

    public synchronized void addMeasurement(ZonedDateTime time, Integer measurement) {
        sensorState.addMeasurement(time, measurement);
        sensorMetrics.addMeasurement(time, measurement);
    }

    public SensorStatus status() {
        return sensorState.status();
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

}
