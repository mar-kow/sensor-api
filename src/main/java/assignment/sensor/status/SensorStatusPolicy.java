package assignment.sensor.status;

import assignment.sensor.alert.AlertListener;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.EvictingQueue;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NotThreadSafe
public class SensorStatusPolicy {

    private static final Integer THRESHOLD = 2_000;
    private static final int MEASUREMENT_BUFFER_SIZE = 3;

    private final EvictingQueue<Integer> measurementRingBuffer = EvictingQueue.create(MEASUREMENT_BUFFER_SIZE);
    private final AlertListener alertListener;
    private SensorStatus status = SensorStatus.OK;

    public SensorStatusPolicy(AlertListener alertListener) {
        this.alertListener = alertListener;
    }

    public void addMeasurement(ZonedDateTime time, Integer measurement) {
        measurementRingBuffer.add(measurement);
        var previousStatus = status;
        calculateStatus(measurement);
        handleAlerts(time, previousStatus);
    }

    public SensorStatus status() {
        return status;
    }

    private void handleAlerts(ZonedDateTime time, SensorStatus previousStatus) {
        if (hasStatusChangedToAlert(previousStatus)) {
            alertListener.onNewAlertCreated(time, collectMeasurements());
        } else if (hasStatusChangedFromAlertToOk(previousStatus)) {
            alertListener.onAlertClosed(time);
        }
    }

    private List<Integer> collectMeasurements() {
        return measurementRingBuffer.stream()
                                    .collect(Collectors.toUnmodifiableList());
    }

    private boolean hasStatusChangedToAlert(SensorStatus previousStatus) {
        return status == SensorStatus.ALERT && previousStatus != status;
    }

    private boolean hasStatusChangedFromAlertToOk(SensorStatus previousStatus) {
        return status == SensorStatus.OK && previousStatus == SensorStatus.ALERT;
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

    private boolean allMeasurementsAboveThreshold() {
        return isMeasurementRingBufferFull() && measurementRingBuffer.stream()
                                                                     .allMatch(measurement -> measurement > THRESHOLD);
    }

    private boolean isMeasurementRingBufferFull() {
        return measurementRingBuffer.size() == MEASUREMENT_BUFFER_SIZE;
    }

    @VisibleForTesting
    EvictingQueue<Integer> measurementRingBuffer() {
        return measurementRingBuffer;
    }

}
