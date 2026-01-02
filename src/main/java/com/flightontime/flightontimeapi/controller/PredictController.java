package com.flightontime.flightontimeapi.controller;

import com.flightontime.flightontimeapi.dto.ValidacionGrupos;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.service.FlightPredictionService;

@RestController
@RequestMapping("/predict")
public class PredictController {

    private final FlightPredictionService service;

    public PredictController(FlightPredictionService service) {
        this.service = service;
    }

    @PostMapping
    public FlightPredictionDTO predict(
            @Validated(ValidacionGrupos.SecuenciaOrdenada.class) @RequestBody FlightRequestDTO request) {

        return service.predecirVuelo(request);
    }
}
