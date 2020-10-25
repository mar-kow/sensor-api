package assignment.sensor.metrics;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DailySensorMetricsTest {

    @Test
    public void shouldCalculateMetricsForNoMeasurements() {
        DailySensorMetrics dailyMetrics = new DailySensorMetrics();

        assertThat(dailyMetrics.averageMeasurement()).isEqualTo(0);
        assertThat(dailyMetrics.maxMeasurement()).isEqualTo(0);
    }

    @Test
    public void shouldCalculateMetricsForSingleMeasurement() {
        DailySensorMetrics dailyMetrics = new DailySensorMetrics();
        givenMeasurements(dailyMetrics, 10);

        assertThat(dailyMetrics.averageMeasurement()).isEqualTo(10);
        assertThat(dailyMetrics.maxMeasurement()).isEqualTo(10);
    }

    @Test
    public void shouldCalculateMetricsForTwoMeasurements() {
        DailySensorMetrics dailyMetrics = new DailySensorMetrics();
        givenMeasurements(dailyMetrics, 10, 20);

        assertThat(dailyMetrics.averageMeasurement()).isEqualTo(15);
        assertThat(dailyMetrics.maxMeasurement()).isEqualTo(20);
    }

    @Test
    public void shouldCalculateMetricsForThreeMeasurements() {
        DailySensorMetrics dailyMetrics = new DailySensorMetrics();
        givenMeasurements(dailyMetrics, 30, 20, 10);

        assertThat(dailyMetrics.averageMeasurement()).isEqualTo(20);
        assertThat(dailyMetrics.maxMeasurement()).isEqualTo(30);
    }

    @Test
    public void shouldCalculateMetricsForFourMeasurements() {
        DailySensorMetrics dailyMetrics = new DailySensorMetrics();
        givenMeasurements(dailyMetrics, 30, 20, 10, 100);

        assertThat(dailyMetrics.averageMeasurement()).isEqualTo(40);
        assertThat(dailyMetrics.maxMeasurement()).isEqualTo(100);
    }

    @Test
    public void shouldCalculateMetricsForFiveMeasurements() {
        DailySensorMetrics dailyMetrics = new DailySensorMetrics();
        givenMeasurements(dailyMetrics, 30, 20, 150, 10, 100);

        assertThat(dailyMetrics.averageMeasurement()).isEqualTo(61);
        assertThat(dailyMetrics.maxMeasurement()).isEqualTo(150);
    }

    private void givenMeasurements(DailySensorMetrics dailyMetrics, int... measurements) {
        Arrays.stream(measurements)
              .forEach(dailyMetrics::addMeasurement);
    }

}