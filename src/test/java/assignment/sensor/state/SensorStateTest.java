package assignment.sensor.state;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensorStateTest {

    @Test
    public void shouldHaveDefaultStatusSetToOk() {
        SensorState sensorState = new SensorState();

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
    }

    @Test
    public void shouldAddMeasurements() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(1);
        sensorState.addMeasurement(2);
        sensorState.addMeasurement(3);

        assertThat(sensorState.measurementRingBuffer()).containsExactly(1, 2, 3);
    }

    @Test
    public void shouldAddMeasurementsAndRemoveHeadWhenMeasurementRingBufferIsFull() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(1);
        sensorState.addMeasurement(2);
        sensorState.addMeasurement(3);
        sensorState.addMeasurement(4);
        sensorState.addMeasurement(5);

        assertThat(sensorState.measurementRingBuffer()).containsExactly(3, 4, 5);
    }

    @Test
    public void shouldHaveStatusOkForOneBelowThresholdMeasurement() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(1_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
    }

    @Test
    public void shouldHaveStatusOkForTwoBelowThresholdMeasurements() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(1_000);
        sensorState.addMeasurement(1_500);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
    }

    @Test
    public void shouldHaveStatusOkForThreeBelowOrEqualThresholdMeasurements() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(1_000);
        sensorState.addMeasurement(1_500);
        sensorState.addMeasurement(2_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
    }

    @Test
    public void shouldHaveStatusWarnForOneAboveThresholdMeasurement() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(2_001);

        assertThat(sensorState.status()).isSameAs(SensorStatus.WARN);
    }

    @Test
    public void shouldHaveStatusWarnForTwoAboveThresholdMeasurements() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(2_001);
        sensorState.addMeasurement(2_500);

        assertThat(sensorState.status()).isSameAs(SensorStatus.WARN);
    }

    @Test
    public void shouldHaveStatusAlertForThreeAboveThresholdMeasurements() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(2_001);
        sensorState.addMeasurement(2_500);
        sensorState.addMeasurement(3_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.ALERT);
    }

    @Test
    public void shouldHaveStatusWarnForOneBelowAndOneAboveThresholdMeasurements() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(1_000);
        sensorState.addMeasurement(2_001);

        assertThat(sensorState.status()).isSameAs(SensorStatus.WARN);
    }

    @Test
    public void shouldClearStatusToOkAfterThreeBelowThresholdMeasurements() {
        SensorState sensorState = new SensorState();
        sensorState.addMeasurement(2_001);
        sensorState.addMeasurement(2_500);
        sensorState.addMeasurement(3_000);

        sensorState.addMeasurement(1_000);
        sensorState.addMeasurement(1_000);
        sensorState.addMeasurement(1_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
    }

}