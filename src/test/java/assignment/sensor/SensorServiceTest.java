package assignment.sensor;

import assignment.sensor.alert.Alert;
import assignment.sensor.metrics.CalculatedSensorMetrics;
import assignment.sensor.status.SensorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static assignment.sensor.TestUtils.time;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    Sensor sensor;

    @Test
    public void shouldAddMeasurementToNewSensor() {
        var sensorService = new SensorService(() -> sensor);
        var time = time(2020, 10, 25, 13, 0, 0);

        sensorService.addMeasurement("id", time, 1_000);

        assertThat(sensorService.sensors()).hasSize(1).containsExactly(entry("id", sensor));
        verify(sensor).addMeasurement(eq(time), eq(1_000));
    }

    @Test
    public void shouldAddMeasurementToExistingSensor() {
        var sensorService = new SensorService();
        sensorService.sensors().put("id", sensor);
        var time = time(2020, 10, 25, 13, 0, 0);

        sensorService.addMeasurement("id", time, 1_000);

        assertThat(sensorService.sensors()).hasSize(1).containsExactly(entry("id", sensor));
        verify(sensor).addMeasurement(eq(time), eq(1_000));
    }

    @Test
    public void shouldReturnStatusForExistingSensor() {
        var sensorService = new SensorService();
        sensorService.sensors().put("id", sensor);
        when(sensor.status()).thenReturn(SensorStatus.OK);

        var result = sensorService.getStatusFor("id");

        assertThat(result).isSameAs(SensorStatus.OK);
        verify(sensor).status();
    }

    @Test()
    public void shouldThrowSensorNotFoundExceptionWhileGettingStatusForNonExistingSensor() {
        var sensorService = new SensorService();

        assertThatThrownBy(() -> sensorService.getStatusFor("id")).isInstanceOf(SensorNotFoundException.class);
    }

    @Test
    public void shouldReturnMetricsForExistingSensor() {
        var sensorService = new SensorService();
        sensorService.sensors().put("id", sensor);
        var metrics = new CalculatedSensorMetrics(2_000, 1_000);
        when(sensor.metrics()).thenReturn(metrics);

        var result = sensorService.getMetricsFor("id");

        assertThat(result).isSameAs(metrics);
        verify(sensor).metrics();
    }

    @Test()
    public void shouldThrowSensorNotFoundExceptionWhileGettingMetricsForNonExistingSensor() {
        var sensorService = new SensorService();

        assertThatThrownBy(() -> sensorService.getMetricsFor("id")).isInstanceOf(SensorNotFoundException.class);
    }

    @Test
    public void shouldListAlertsForExistingSensor() {
        var sensorService = new SensorService();
        sensorService.sensors().put("id", sensor);
        var alerts = new ArrayList<Alert>();
        when(sensor.alerts()).thenReturn(alerts);

        var result = sensorService.listAlertsFor("id");

        assertThat(result).isSameAs(alerts);
        verify(sensor).alerts();
    }

    @Test()
    public void shouldThrowSensorNotFoundExceptionWhileListingAlertsForNonExistingSensor() {
        var sensorService = new SensorService();

        assertThatThrownBy(() -> sensorService.listAlertsFor("id")).isInstanceOf(SensorNotFoundException.class);
    }

}