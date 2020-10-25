package assignment.sensor;

public class SensorNotFoundException extends RuntimeException {

    public SensorNotFoundException(String sensorId) {
        super("No sensor found with sensorId=" + sensorId);
    }
}
