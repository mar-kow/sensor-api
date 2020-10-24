package assignment.sensor.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldCollectMeasurement() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sensors/{uuid}/measurements", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content("{\"co2\" : 2000, \"time\" : \"2019-02-01T18:55:47+00:00\"}"))
               .andExpect(status().isNoContent());
    }

    @Test
    public void shouldGetStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void shouldGetMetrics() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}/metrics", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.maxLast30Days", is(1_000)))
               .andExpect(jsonPath("$.avgLast30Days", is(1_000)));
    }

    @Test
    public void shouldListAlerts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sensors/{uuid}/alerts", "4af96470-aebc-4c32-a1f7-da450a2d0bf8")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.empty()));
    }

}