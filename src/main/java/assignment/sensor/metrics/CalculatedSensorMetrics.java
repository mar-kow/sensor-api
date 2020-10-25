package assignment.sensor.metrics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalculatedSensorMetrics implements SensorMetrics {

    private final int maxMeasurement;
    private final int averageMeasurement;

    @Override
    public int maxMeasurement() {
        return maxMeasurement;
    }

    @Override
    public int averageMeasurement() {
        return averageMeasurement;
    }
}
