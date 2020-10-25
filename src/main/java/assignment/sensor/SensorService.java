package assignment.sensor;

import assignment.sensor.metrics.SensorMetrics;
import assignment.sensor.alert.Alert;
import assignment.sensor.status.SensorStatus;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

@Service
public class SensorService {

    private final ConcurrentMap<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Supplier<Sensor> sensorSupplier;

    public SensorService() {
        this(() -> new Sensor());
    }

    @VisibleForTesting
    SensorService(Supplier<Sensor> sensorSupplier) {
        this.sensorSupplier = sensorSupplier;
    }

    public void addMeasurement(String sensorId, ZonedDateTime time, Integer measurement) {
        var sensor = sensors.computeIfAbsent(sensorId, id -> sensorSupplier.get());
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

    public List<Alert> listAlertsFor(String sensorId) {
        var sensor = findSensor(sensorId);
        return sensor.alerts();
    }

    private Sensor findSensor(String sensorId) {
        var sensor = sensors.get(sensorId);
        if (sensor == null) {
            throw new SensorNotFoundException(sensorId);
        }
        return sensor;
    }

    @VisibleForTesting
    ConcurrentMap<String, Sensor> sensors() {
        return sensors;
    }

}
