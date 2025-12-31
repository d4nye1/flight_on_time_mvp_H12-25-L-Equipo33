package com.flightontime.flightontimeapi.controller;

import com.flightontime.flightontimeapi.dto.ValidacionGrupos;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.service.FlightPredictionService;

//@RestController
//@RequestMapping("/predict")
@RestController
@RequestMapping("/api/flights")
public class PredictController {

    private final FlightPredictionService service;

    public PredictController(FlightPredictionService service) {
        this.service = service;
    }

    //@PostMapping
    @PostMapping("/predict")
    public FlightPredictionDTO predict(
            @Validated(ValidacionGrupos.SecuenciaOrdenada.class) @RequestBody FlightRequestDTO request) {

        return service.predecirVuelo(request);
    }
}
