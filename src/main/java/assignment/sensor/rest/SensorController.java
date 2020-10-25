package assignment.sensor.rest;

import assignment.sensor.SensorService;
import assignment.sensor.alert.Alert;
import assignment.sensor.metrics.SensorMetrics;
import assignment.sensor.rest.dto.AlertDto;
import assignment.sensor.rest.dto.CollectMeasurementRequest;
import assignment.sensor.rest.dto.GetMetricsResponse;
import assignment.sensor.rest.dto.GetStatusResponse;
import assignment.sensor.status.SensorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/sensors")
public class SensorController {

    private final SensorService sensorService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "{uuid}/measurements", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void collectMeasurement(@PathVariable("uuid") String uuid, @RequestBody CollectMeasurementRequest request) {
        sensorService.addMeasurement(uuid, request.getTime(), request.getCo2());
    }

    @GetMapping(value = "{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetStatusResponse getStatus(@PathVariable("uuid") String uuid) {
        SensorStatus sensorStatus = sensorService.getStatusFor(uuid);
        return new GetStatusResponse(sensorStatus.name());
    }

    @GetMapping(value = "{uuid}/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetMetricsResponse getMetrics(@PathVariable("uuid") String uuid) {
        SensorMetrics sensorMetrics = sensorService.getMetricsFor(uuid);
        return new GetMetricsResponse(sensorMetrics.maxMeasurement(), sensorMetrics.averageMeasurement());
    }

    @GetMapping(value = "{uuid}/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AlertDto> listAlerts(@PathVariable("uuid") String uuid) {
        return sensorService.listAlertsFor(uuid)
                            .stream()
                            .map(this::asDto)
                            .collect(Collectors.toList());
    }

    private AlertDto asDto(Alert alert) {
        var measurements = alert.getMeasurements();
        return AlertDto.builder()
                       .startTime(alert.getStartTime())
                       .endTime(alert.getEndTime())
                       .measurement1(measurements.get(0))
                       .measurement2(measurements.get(1))
                       .measurement3(measurements.get(2))
                       .build();
    }

}
