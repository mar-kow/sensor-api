package assignment.sensor;

import assignment.sensor.metrics.SensorMetrics;
import assignment.sensor.alert.Alert;
import assignment.sensor.state.SensorStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SensorService {

    private final ConcurrentMap<String, Sensor> sensors = new ConcurrentHashMap<>();

    public void addMeasurement(String sensorId, ZonedDateTime time, Integer measurement) {
        var sensor = sensors.computeIfAbsent(sensorId, id -> new Sensor());
        sensor.addMeasurement(time, measurement);
    }

    public SensorStatus getStatusFor(String sensorId) {
        var sensor = findSensor(sensorId);
        return sensor.status();
    }

    public SensorMetrics getMetricsFor(String sensorId) {
        var sensor = findSensor(sensorId);
        return sensor.metrics();
    }

    private Sensor findSensor(String sensorId) {
        var sensor = sensors.get(sensorId);
        if (sensor == null) {
            throw new SensorNotFoundException(sensorId);
        }
        return sensor;
    }

    public List<Alert> listAlerts(String sensorId) {
        var sensor = findSensor(sensorId);
        return sensor.alerts();
    }
}
