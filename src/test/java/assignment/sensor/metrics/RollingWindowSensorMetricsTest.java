package assignment.sensor.metrics;

import org.junit.jupiter.api.Test;

import static assignment.sensor.TestUtils.date;
import static assignment.sensor.TestUtils.time;
import static org.assertj.core.api.Assertions.assertThat;

class RollingWindowSensorMetricsTest {

    @Test
    public void shouldAddNewDailyEntries() {
        RollingWindowSensorMetrics rollingWindowSensorMetrics = new RollingWindowSensorMetrics(3);

        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 26, 13, 0, 0), 1_100);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 27, 13, 0, 0), 1_200);

        assertThat(rollingWindowSensorMetrics.dailyMetricsRingBuffer()).hasSize(3);
        var entry1 = rollingWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry1.getDate()).isEqualTo(date(2020, 10, 25));
        assertThat(entry1.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_000);
        assertThat(entry1.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_000);
        var entry2 = rollingWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry2.getDate()).isEqualTo(date(2020, 10, 26));
        assertThat(entry2.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_100);
        assertThat(entry2.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_100);
        var entry3 = rollingWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry3.getDate()).isEqualTo(date(2020, 10, 27));
        assertThat(entry3.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_200);
        assertThat(entry3.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_200);
    }

    @Test
    public void shouldAddNewDailyEntriesByDeletingHeadWhenRingBufferIsFull() {
        RollingWindowSensorMetrics rollingWindowSensorMetrics = new RollingWindowSensorMetrics(3);

        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 26, 13, 0, 0), 1_100);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 27, 13, 0, 0), 1_200);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 28, 13, 0, 0), 1_300);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 29, 13, 0, 0), 1_400);

        assertThat(rollingWindowSensorMetrics.dailyMetricsRingBuffer()).hasSize(3);
        var entry1 = rollingWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry1.getDate()).isEqualTo(date(2020, 10, 27));
        assertThat(entry1.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_200);
        assertThat(entry1.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_200);
        var entry2 = rollingWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry2.getDate()).isEqualTo(date(2020, 10, 28));
        assertThat(entry2.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_300);
        assertThat(entry2.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_300);
        var entry3 = rollingWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry3.getDate()).isEqualTo(date(2020, 10, 29));
        assertThat(entry3.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_400);
        assertThat(entry3.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_400);
    }

    @Test
    public void shouldUpdateExistingDailyEntry() {
        RollingWindowSensorMetrics rollingWindowSensorMetrics = new RollingWindowSensorMetrics(3);

        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_100);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_200);

        assertThat(rollingWindowSensorMetrics.dailyMetricsRingBuffer()).hasSize(1);
        var entry1 = rollingWindowSensorMetrics.dailyMetricsRingBuffer().poll();
        assertThat(entry1.getDate()).isEqualTo(date(2020, 10, 25));
        assertThat(entry1.getDailySensorMetrics().maxMeasurement()).isEqualTo(1_200);
        assertThat(entry1.getDailySensorMetrics().averageMeasurement()).isEqualTo(1_100);
    }

    @Test
    public void shouldCalculateMetricsCoveringAllDays() {
        RollingWindowSensorMetrics rollingWindowSensorMetrics = new RollingWindowSensorMetrics(3);

        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 25, 13, 0, 0), 1_000);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 26, 13, 0, 0), 3_000);
        rollingWindowSensorMetrics.addMeasurement(time(2020, 10, 27, 13, 0, 0), 2_500);

        assertThat(rollingWindowSensorMetrics.maxMeasurement()).isEqualTo(3_000);
        assertThat(rollingWindowSensorMetrics.averageMeasurement()).isEqualTo(2166);
    }

}