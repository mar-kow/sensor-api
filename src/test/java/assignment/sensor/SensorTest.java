package assignment.sensor;

import assignment.sensor.alert.Alert;
import assignment.sensor.metrics.CalculatedSensorMetrics;
import assignment.sensor.metrics.RollingWindowSensorMetrics;
import assignment.sensor.status.SensorStatus;
import assignment.sensor.status.SensorStatusPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static assignment.sensor.TestUtils.time;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorTest {

    @Mock
    SensorStatusPolicy sensorStatusPolicy;

    @Mock
    RollingWindowSensorMetrics rollingWindowSensorMetrics;

    @Test
    public void shouldAddMeasurement() {
        var sensor = new Sensor(sensorStatusPolicy, rollingWindowSensorMetrics);
        ZonedDateTime time = time(2020, 10, 25, 13, 0, 0);

        sensor.addMeasurement(time, 2_000);

        InOrder inOrder = inOrder(sensorStatusPolicy, rollingWindowSensorMetrics);
        inOrder.verify(sensorStatusPolicy).addMeasurement(eq(time), eq(2_000));
        inOrder.verify(rollingWindowSensorMetrics).addMeasurement(eq(time), eq(2_000));
    }

    @Test
    public void shouldReturnStatus() {
        var sensor = new Sensor(sensorStatusPolicy, rollingWindowSensorMetrics);
        when(sensorStatusPolicy.status()).thenReturn(SensorStatus.OK);

        var result = sensor.status();

        assertThat(result).isSameAs(SensorStatus.OK);
        verify(sensorStatusPolicy).status();
    }

    @Test
    public void shouldReturnMetrics() {
        var sensor = new Sensor(sensorStatusPolicy, rollingWindowSensorMetrics);
        when(rollingWindowSensorMetrics.maxMeasurement()).thenReturn(2_000);
        when(rollingWindowSensorMetrics.averageMeasurement()).thenReturn(1_000);

        var results = sensor.metrics();

        assertThat(results).isEqualTo(new CalculatedSensorMetrics(2_000, 1_000));
        verify(rollingWindowSensorMetrics).maxMeasurement();
        verify(rollingWindowSensorMetrics).averageMeasurement();
    }

    @Test
    public void shouldAppendNewAlert() {
        var sensor = new Sensor(sensorStatusPolicy, rollingWindowSensorMetrics);
        var startTime = time(2020, 10, 25, 13, 0, 0);
        var measurements = List.of(2_000, 3_000, 4_000);

        sensor.alertListener().onNewAlertCreated(startTime, measurements);

        assertThat(sensor.alerts()).hasSize(1).contains(new Alert(startTime, measurements));
    }

    @Test
    public void shouldCloseAlert() {
        var sensor = new Sensor(sensorStatusPolicy, rollingWindowSensorMetrics);
        var startTime = time(2020, 10, 25, 13, 0, 0);
        var endTime = time(2020, 10, 25, 14, 0, 0);
        var measurements = List.of(2_000, 3_000, 4_000);

        sensor.alertListener().onNewAlertCreated(startTime, measurements);
        sensor.alertListener().onAlertClosed(endTime);

        assertThat(sensor.alerts()).hasSize(1).contains(expectedClosedAlert(startTime, endTime, measurements));
    }

    private Alert expectedClosedAlert(ZonedDateTime startTime, ZonedDateTime endTime, List<Integer> measurements) {
        var alert = new Alert(startTime, measurements);
        alert.setEndTime(endTime);
        return alert;
    }

    private List<Alert> alerts() {
        ZonedDateTime startTime = time(2020, 10, 25, 13, 0, 0);
        return List.of(new Alert(startTime, List.of(2_000, 3_000, 4_000)));
    }

}