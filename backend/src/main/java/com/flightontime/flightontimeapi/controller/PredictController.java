package com.flightontime.flightontimeapi.controller;

import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionWithStatsDTO;
import com.flightontime.flightontimeapi.service.FlightPredictionService;
import com.flightontime.flightontimeapi.dto.ValidacionGrupos;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;


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
    public ResponseEntity<?> predictWithStats(
            @Validated(ValidacionGrupos.SecuenciaOrdenada.class)
            @RequestBody FlightRequestDTO request) {

        try {
            FlightPredictionWithStatsDTO response =
                    service.predecirVueloConStats(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // 👈 CLAVE para ver el error real

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "ERROR",
                            "message", "Error al generar predicción con estadísticas"
                    ));
        }
    }


}