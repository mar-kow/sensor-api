package assignment.sensor.metrics;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
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
