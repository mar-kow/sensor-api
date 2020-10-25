package assignment.sensor.rest;

import assignment.sensor.SensorNotFoundException;
import assignment.sensor.SensorService;
import assignment.sensor.alert.Alert;
import assignment.sensor.metrics.CalculatedSensorMetrics;
import assignment.sensor.status.SensorStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZonedDateTime;
import java.util.List;

import static assignment.sensor.TestUtils.time;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SensorControllerTest {

    @MockBean
    SensorService sensorService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldCollectMeasurement() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sensors/{uuid}/measurements", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content("{\"co2\" : 2000, \"time\" : \"2019-02-01T18:55:47+00:00\"}"))
               .andExpect(status().isNoContent());

        ArgumentCaptor<ZonedDateTime> timeCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
        verify(sensorService).addMeasurement(eq("4af96470-aebc-4c32-a1f7-da450a2d0bf8"), timeCaptor.capture(), eq(2000));
        Assertions.assertThat(timeCaptor.getValue()).isEqualTo(time(2019, 2, 1, 18, 55, 47));
    }

    @Test
    public void shouldGetStatus() throws Exception {
        when(sensorService.getStatusFor(any())).thenReturn(SensorStatus.OK);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status", is("OK")));

        verify(sensorService).getStatusFor(eq("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));
    }

    @Test
    public void shouldReturnNotFoundWhileGettingStatusForNonExistingSensor() throws Exception {
        when(sensorService.getStatusFor(any())).thenThrow(new SensorNotFoundException("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());

        verify(sensorService).getStatusFor(eq("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));
    }

    @Test
    public void shouldGetMetrics() throws Exception {
        when(sensorService.getMetricsFor(any())).thenReturn(new CalculatedSensorMetrics(1_000, 900));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}/metrics", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.maxLast30Days", is(1_000)))
               .andExpect(jsonPath("$.avgLast30Days", is(900)));

        verify(sensorService).getMetricsFor(eq("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));
    }

    @Test
    public void shouldReturnNotFoundWhileGettingMetricsForNonExistingSensor() throws Exception {
        when(sensorService.getMetricsFor(any())).thenThrow(new SensorNotFoundException("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}/metrics", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());

        verify(sensorService).getMetricsFor(eq("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));
    }

    @Test
    public void shouldListAlerts() throws Exception {
        when(sensorService.listAlerts(any())).thenReturn(List.of(alert()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}/alerts", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andDo(print())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].startTime", is("2019-02-01T18:55:00Z")))
               .andExpect(jsonPath("$[0].endTime", is("2019-02-01T18:59:00Z")))
               .andExpect(jsonPath("$[0].measurement1", is(2_000)))
               .andExpect(jsonPath("$[0].measurement2", is(2_001)))
               .andExpect(jsonPath("$[0].measurement3", is(2_002)));

        verify(sensorService).listAlerts(eq("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));
    }

    @Test
    public void shouldReturnNotFoundWhileListingAlertsForNonExistingSensor() throws Exception {
        when(sensorService.listAlerts(any())).thenThrow(new SensorNotFoundException("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}/alerts", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());

        verify(sensorService).listAlerts(eq("4af96470-aebc-4c32-a1f7-da450a2d0bf8"));
    }

    private Alert alert() {
        var measurements = List.of(2_000, 2_001, 2_002);
        var alert = new Alert(time(2019, 2, 1, 18, 55, 00), measurements);
        alert.setEndTime(time(2019, 2, 1, 18, 59, 00));
        return alert;
    }

}