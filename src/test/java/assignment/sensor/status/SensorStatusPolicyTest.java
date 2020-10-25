package assignment.sensor.status;

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
class SensorStatusPolicyTest {

    @Mock
    AlertListener alertListener;

    @Test
    public void shouldHaveDefaultStatusSetToOk() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.OK);
    }

    @Test
    public void shouldAddMeasurements() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 1);
        sensorStatusPolicy.addMeasurement(time, 2);
        sensorStatusPolicy.addMeasurement(time, 3);

        assertThat(sensorStatusPolicy.measurementRingBuffer()).containsExactly(1, 2, 3);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldAddMeasurementsAndRemoveHeadWhenMeasurementRingBufferIsFull() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 1);
        sensorStatusPolicy.addMeasurement(time, 2);
        sensorStatusPolicy.addMeasurement(time, 3);
        sensorStatusPolicy.addMeasurement(time, 4);
        sensorStatusPolicy.addMeasurement(time, 5);

        assertThat(sensorStatusPolicy.measurementRingBuffer()).containsExactly(3, 4, 5);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusOkForOneBelowThresholdMeasurement() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 1_000);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.OK);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusOkForTwoBelowThresholdMeasurements() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 1_000);
        sensorStatusPolicy.addMeasurement(time, 1_500);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.OK);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusOkForThreeBelowOrEqualThresholdMeasurements() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 1_000);
        sensorStatusPolicy.addMeasurement(time, 1_500);
        sensorStatusPolicy.addMeasurement(time, 2_000);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.OK);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusWarnForOneAboveThresholdMeasurement() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 2_001);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.WARN);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusWarnForTwoAboveThresholdMeasurements() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 2_001);
        sensorStatusPolicy.addMeasurement(time, 2_500);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.WARN);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusAlertForThreeAboveThresholdMeasurements() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time1 = time(2020, 10, 25, 13, 0, 0);
        ZonedDateTime time2 = time(2020, 10, 25, 13, 1, 0);
        ZonedDateTime time3 = time(2020, 10, 25, 13, 2, 0);

        sensorStatusPolicy.addMeasurement(time1, 2_001);
        sensorStatusPolicy.addMeasurement(time2, 2_500);
        sensorStatusPolicy.addMeasurement(time3, 3_000);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.ALERT);
        verify(alertListener).onNewAlertCreated(eq(time3), eq(List.of(2_001, 2_500, 3_000)));
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldHaveStatusWarnForOneBelowAndOneAboveThresholdMeasurements() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensorStatusPolicy.addMeasurement(time, 1_000);
        sensorStatusPolicy.addMeasurement(time, 2_001);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.WARN);
        verify(alertListener, never()).onNewAlertCreated(any(), any());
        verify(alertListener, never()).onAlertClosed(any());
    }

    @Test
    public void shouldClearStatusToOkAfterThreeBelowThresholdMeasurements() {
        SensorStatusPolicy sensorStatusPolicy = new SensorStatusPolicy(alertListener);
        ZonedDateTime time1 = time(2020, 10, 25, 13, 0, 0);
        ZonedDateTime time2 = time(2020, 10, 25, 13, 1, 0);
        ZonedDateTime time3 = time(2020, 10, 25, 13, 2, 0);
        ZonedDateTime time4 = time(2020, 10, 25, 13, 3, 0);
        ZonedDateTime time5 = time(2020, 10, 25, 13, 4, 0);
        ZonedDateTime time6 = time(2020, 10, 25, 13, 5, 0);

        sensorStatusPolicy.addMeasurement(time1, 2_001);
        sensorStatusPolicy.addMeasurement(time2, 2_500);
        sensorStatusPolicy.addMeasurement(time3, 3_000);
        sensorStatusPolicy.addMeasurement(time4, 1_000);
        sensorStatusPolicy.addMeasurement(time5, 1_000);
        sensorStatusPolicy.addMeasurement(time6, 1_000);

        assertThat(sensorStatusPolicy.status()).isSameAs(SensorStatus.OK);
        verify(alertListener).onNewAlertCreated(eq(time3), eq(List.of(2_001, 2_500, 3_000)));
        verify(alertListener).onAlertClosed(eq(time6));
    }

}