package com.flightontime.flightontimeapi.controller;

import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionWithStatsDTO;
import com.flightontime.flightontimeapi.service.FlightPredictionService;
import com.flightontime.flightontimeapi.dto.ValidacionGrupos;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class PredictController {

    private final FlightPredictionService service;

    public PredictController(FlightPredictionService service) {
        this.service = service;
    }

    @PostMapping("/predict")
    public FlightPredictionDTO predict(
            @Validated(ValidacionGrupos.SecuenciaOrdenada.class) @RequestBody FlightRequestDTO request) {
        return service.predecirVuelo(request);
    }
    @PostMapping("/predict-with-stats")
    public FlightPredictionWithStatsDTO predictWithStats(
            @Validated(ValidacionGrupos.SecuenciaOrdenada.class) @RequestBody FlightRequestDTO request) {
        return service.predecirVueloConStats(request);
    }

}