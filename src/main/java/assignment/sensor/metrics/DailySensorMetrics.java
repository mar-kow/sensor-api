package assignment.sensor.metrics;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * To keep memory footprint low and avoid OutOfMemoryException, measurements are not stored and averages are estimated
 * with running average algorithm described here: https://math.stackexchange.com/questions/106700/incremental-averageing
 */
@NotThreadSafe
public class DailySensorMetrics implements SensorMetrics {

    private int maxMeasurement;
    private int averageMeasurement;
    private int count;

    public void addMeasurement(int measurement) {
        count++;
        averageMeasurement = calculateAverage(measurement);
        maxMeasurement = calculateMax(measurement);
    }

    @Override
    public int maxMeasurement() {
        return maxMeasurement;
    }

    @Override
    public int averageMeasurement() {
        return averageMeasurement;
    }

    private int calculateAverage(int measurement) {
        return ((count - 1) * averageMeasurement + measurement) / count;
    }

    private int calculateMax(int measurement) {
        return Math.max(maxMeasurement, measurement);
    }

}
