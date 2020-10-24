package assignment.sensor.rest;

import assignment.sensor.rest.dto.AlertDto;
import assignment.sensor.rest.dto.CollectMeasurementRequest;
import assignment.sensor.rest.dto.GetMetricsResponse;
import assignment.sensor.rest.dto.GetStatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/sensors")
public class SensorController {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "{uuid}/measurements", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void collectMeasurement(@PathParam("uuid") String uuid, @RequestBody CollectMeasurementRequest request) {
    }

    @GetMapping(value = "{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetStatusResponse getStatus(@PathParam("uuid") String uuid) {
        return new GetStatusResponse(GetStatusResponse.Status.OK);
    }

    @GetMapping(value = "{uuid}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetMetricsResponse getMetrics(@PathParam("uuid") String uuid) {
        return new GetMetricsResponse(1_000, 1_000);
    }

    @GetMapping(value = "{uuid}/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AlertDto> listAlerts(@PathParam("uuid") String uuid) {
        return Collections.emptyList();
    }

}
