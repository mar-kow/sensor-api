package assignment.sensor.metrics;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.EvictingQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * To keep memory footprint low and avoid OutOfMemoryException, measurement samples are not stored.
 * Metrics are kept for each of last N days separately, hence:
 * average measurement is average of all available daily averages.
 * max measurement is max of all max daily measurements.
 * When number of measurements for each day is similar, estimation error for average measurement stays low.
 */
@NotThreadSafe
public class RollingWindowSensorMetrics implements SensorMetrics {

    private final EvictingQueue<Entry> dailyMetricsRingBuffer;

    public RollingWindowSensorMetrics(int numberOfDays) {
        this.dailyMetricsRingBuffer = EvictingQueue.create(numberOfDays);
    }

    @Override
    public int maxMeasurement() {
        return dailyMetricsRingBuffer.stream()
                                     .map(entry -> entry.getDailySensorMetrics().maxMeasurement())
                                     .collect(Collectors.maxBy(Comparator.comparingInt(x -> x)))
                                     .orElse(0);
    }

    @Override
    public int averageMeasurement() {
        return dailyMetricsRingBuffer.stream()
                                     .collect(Collectors.averagingInt(entry -> entry.getDailySensorMetrics().averageMeasurement()))
                                     .intValue();
    }

    public void addMeasurement(ZonedDateTime time, int measurement) {
        var entry = findEntryFor(time);
        entry.addMeasurement(measurement);
    }

    private Entry findEntryFor(ZonedDateTime time) {
        var date = time.toLocalDate();
        return dailyMetricsRingBuffer.stream()
                                     .filter(entry -> entry.getDate().equals(date))
                                     .findFirst()
                                     .orElseGet(() -> createNewEntry(date));
    }

    private Entry createNewEntry(LocalDate date) {
        var entry = Entry.of(date, new DailySensorMetrics());
        dailyMetricsRingBuffer.add(entry);
        return entry;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    static class Entry {
        private final LocalDate date;
        private final DailySensorMetrics dailySensorMetrics;

        void addMeasurement(int measurement) {
            dailySensorMetrics.addMeasurement(measurement);
        }
    }

    @VisibleForTesting
    EvictingQueue<Entry> dailyMetricsRingBuffer() {
        return dailyMetricsRingBuffer;
    }

}
