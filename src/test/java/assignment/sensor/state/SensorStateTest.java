package assignment.sensor.state;

import assignment.sensor.alert.AlertListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static assignment.sensor.TestUtils.time;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SensorStateTest {

    @Mock
    AlertListener alertListener;

    @Test
    public void shouldHaveDefaultStatusSetToOk() {
        SensorState sensorState = new SensorState(alertListener);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
    }

    @Test
    public void shouldAddMeasurements() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 1);
        sensorState.addMeasurement(time, 2);
        sensorState.addMeasurement(time, 3);

        assertThat(sensorState.measurementRingBuffer()).containsExactly(1, 2, 3);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldAddMeasurementsAndRemoveHeadWhenMeasurementRingBufferIsFull() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 1);
        sensorState.addMeasurement(time, 2);
        sensorState.addMeasurement(time, 3);
        sensorState.addMeasurement(time, 4);
        sensorState.addMeasurement(time, 5);

        assertThat(sensorState.measurementRingBuffer()).containsExactly(3, 4, 5);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusOkForOneBelowThresholdMeasurement() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 1_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusOkForTwoBelowThresholdMeasurements() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 1_000);
        sensorState.addMeasurement(time, 1_500);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusOkForThreeBelowOrEqualThresholdMeasurements() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 1_000);
        sensorState.addMeasurement(time, 1_500);
        sensorState.addMeasurement(time, 2_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusWarnForOneAboveThresholdMeasurement() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 2_001);

        assertThat(sensorState.status()).isSameAs(SensorStatus.WARN);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusWarnForTwoAboveThresholdMeasurements() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 2_001);
        sensorState.addMeasurement(time, 2_500);

        assertThat(sensorState.status()).isSameAs(SensorStatus.WARN);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusAlertForThreeAboveThresholdMeasurements() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time1 = time(2020, 10, 25, 13, 0, 0);
        ZonedDateTime time2 = time(2020, 10, 25, 13, 1, 0);
        ZonedDateTime time3 = time(2020, 10, 25, 13, 2, 0);

        sensorState.addMeasurement(time1, 2_001);
        sensorState.addMeasurement(time2, 2_500);
        sensorState.addMeasurement(time3, 3_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.ALERT);
        verify(alertListener).onNewAlertCreated(eq(time3), eq(List.of(2_001, 2_500, 3_000)));
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusWarnForOneBelowAndOneAboveThresholdMeasurements() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorState.addMeasurement(time, 1_000);
        sensorState.addMeasurement(time, 2_001);

        assertThat(sensorState.status()).isSameAs(SensorStatus.WARN);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldClearStatusToOkAfterThreeBelowThresholdMeasurements() {
        SensorState sensorState = new SensorState(alertListener);
        ZonedDateTime time1 = time(2020, 10, 25, 13, 0, 0);
        ZonedDateTime time2 = time(2020, 10, 25, 13, 1, 0);
        ZonedDateTime time3 = time(2020, 10, 25, 13, 2, 0);
        ZonedDateTime time4 = time(2020, 10, 25, 13, 3, 0);
        ZonedDateTime time5 = time(2020, 10, 25, 13, 4, 0);
        ZonedDateTime time6 = time(2020, 10, 25, 13, 5, 0);

        sensorState.addMeasurement(time1, 2_001);
        sensorState.addMeasurement(time2, 2_500);
        sensorState.addMeasurement(time3, 3_000);
        sensorState.addMeasurement(time4, 1_000);
        sensorState.addMeasurement(time5, 1_000);
        sensorState.addMeasurement(time6, 1_000);

        assertThat(sensorState.status()).isSameAs(SensorStatus.OK);
        verify(alertListener).onNewAlertCreated(eq(time3), eq(List.of(2_001, 2_500, 3_000)));
        verify(alertListener).onAlertClosed(eq(time6));
    }

}