package com.flightontime.flightontimeapi.service;

import org.springframework.stereotype.Service;
import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;

@Service
public class FlightPredictionService {

    public FlightPredictionDTO predecirVuelo(FlightRequestDTO request) {

        // Simulación (después aquí entra el modelo de Data Science)
        double probabilidad = Math.random();

        String prevision = probabilidad > 0.5
                ? "Retrasado"
                : "Puntual";

        return new FlightPredictionDTO(prevision, probabilidad);
    }
}
