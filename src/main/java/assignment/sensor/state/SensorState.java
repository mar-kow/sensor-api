package assignment.sensor.state;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.EvictingQueue;

public class SensorState {

    private static final Integer THRESHOLD = 2_000;
    private static final int MAX_SIZE = 3;

    private final EvictingQueue<Integer> measurementRingBuffer;
    private SensorStatus status;

    public SensorState() {
        this.measurementRingBuffer = EvictingQueue.create(MAX_SIZE);
        this.status = SensorStatus.OK;
    }

    public synchronized void addMeasurement(Integer measurement) {
        measurementRingBuffer.add(measurement);
        calculateStatus(measurement);
    }

    public SensorStatus status() {
        return status;
    }

    private void calculateStatus(Integer lastMeasurement) {
        if (allMeasurementsAboveThreshold()) {
            this.status = SensorStatus.ALERT;
        } else if (allMeasurementsBelowThreshold()) {
            this.status = SensorStatus.OK;
        } else if (lastMeasurement > THRESHOLD) {
            this.status = SensorStatus.WARN;
        }
    }

    private boolean allMeasurementsBelowThreshold() {
        return isMeasurementRingBufferFull() && measurementRingBuffer.stream()
                                                                     .allMatch(measurement -> measurement < THRESHOLD);
    }

    public boolean allMeasurementsAboveThreshold() {
        return isMeasurementRingBufferFull() && measurementRingBuffer.stream()
                                                                     .allMatch(measurement -> measurement > THRESHOLD);
    }

    public boolean isMeasurementRingBufferFull() {
        return measurementRingBuffer.size() == MAX_SIZE;
    }

    @VisibleForTesting
    EvictingQueue<Integer> measurementRingBuffer() {
        return measurementRingBuffer;
    }
}
