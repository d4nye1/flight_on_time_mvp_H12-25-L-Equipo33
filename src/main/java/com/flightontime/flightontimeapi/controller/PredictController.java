package com.flightontime.flightontimeapi.controller;

import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.service.FlightPredictionService;
import com.flightontime.flightontimeapi.dto.ValidacionGrupos;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights") // Ajustado para que coincida con el fetch del HTML
@CrossOrigin(origins = "*")    // ¡CRÍTICO! Permite que el HTML se conecte
public class PredictController {

    private final FlightPredictionService service;

    public PredictController(FlightPredictionService service) {
        this.service = service;
    }

    @PostMapping("/predict") // Ruta final: /api/flights/predict
    public FlightPredictionDTO predict(
            @Validated(ValidacionGrupos.SecuenciaOrdenada.class) @RequestBody FlightRequestDTO request) {
        return service.predecirVuelo(request);
    }
}