package assignment.sensor.metrics;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RunningWindowSensorMetricsTest {

    @Test
    public void shouldAddNewDailyEntries() {
        RunningWindowSensorMetrics runningWindowSensorMetrics = new RunningWindowSensorMetrics(3);

        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 26, 13, 0, 0), 1_100);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 27, 13, 0, 0), 1_200);

        assertThat(runningWindowSensorMetrics.dailyMetricsRingBuffer()).hasSize(3);
        var entry1 = runningWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry1.getDate()).isEqualTo(date(2020, 10, 25));
        assertThat(entry1.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_000);
        assertThat(entry1.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_000);
        var entry2 = runningWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry2.getDate()).isEqualTo(date(2020, 10, 26));
        assertThat(entry2.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_100);
        assertThat(entry2.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_100);
        var entry3 = runningWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry3.getDate()).isEqualTo(date(2020, 10, 27));
        assertThat(entry3.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_200);
        assertThat(entry3.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_200);
    }

    @Test
    public void shouldAddNewDailyEntriesByDeletingHeadWhenRingBufferIsFull() {
        RunningWindowSensorMetrics runningWindowSensorMetrics = new RunningWindowSensorMetrics(3);

        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 26, 13, 0, 0), 1_100);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 27, 13, 0, 0), 1_200);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 28, 13, 0, 0), 1_300);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 29, 13, 0, 0), 1_400);

        assertThat(runningWindowSensorMetrics.dailyMetricsRingBuffer()).hasSize(3);
        var entry1 = runningWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry1.getDate()).isEqualTo(date(2020, 10, 27));
        assertThat(entry1.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_200);
        assertThat(entry1.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_200);
        var entry2 = runningWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry2.getDate()).isEqualTo(date(2020, 10, 28));
        assertThat(entry2.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_300);
        assertThat(entry2.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_300);
        var entry3 = runningWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry3.getDate()).isEqualTo(date(2020, 10, 29));
        assertThat(entry3.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_400);
        assertThat(entry3.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_400);
    }

    @Test
    public void shouldUpdateExistingDailyEntry() {
        RunningWindowSensorMetrics runningWindowSensorMetrics = new RunningWindowSensorMetrics(3);

        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_100);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_200);

        assertThat(runningWindowSensorMetrics.dailyMetricsRingBuffer()).hasSize(1);
        var entry1 = runningWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry1.getDate()).isEqualTo(date(2020, 10, 25));
        assertThat(entry1.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_200);
        assertThat(entry1.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_100);
    }

    @Test
    public void shouldCalculateMetricsCoveringAllDays() {
        RunningWindowSensorMetrics runningWindowSensorMetrics = new RunningWindowSensorMetrics(3);

        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 26, 13, 0, 0), 3_000);
        runningWindowSensorMetrics.addMeasurement(time(2020, 10, 27, 13, 0, 0), 2_500);

        assertThat(runningWindowSensorMetrics.maxMeasurement()).isEqualTo(3_000);
        assertThat(runningWindowSensorMetrics.averageMeasurement()).isEqualTo(2166);
    }

    private ZonedDateTime time(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, ZoneOffset.UTC);
    }

    private LocalDate date(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }


}