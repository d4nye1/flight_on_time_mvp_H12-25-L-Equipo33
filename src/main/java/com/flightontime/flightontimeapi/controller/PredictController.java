package com.flightontime.flightontimeapi.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
            @Valid @RequestBody FlightRequestDTO request) {

        return service.predecirVuelo(request);
    }
}
