package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class FlightPredictionService {

    private final DataScienceClient dataScienceClient;

    public FlightPredictionService(DataScienceClient dataScienceClient) {
        this.dataScienceClient = dataScienceClient;
    }

    public FlightPredictionDTO predecirVuelo(FlightRequestDTO request) {
        if (request.getOrigen().equalsIgnoreCase(request.getDestino())) {
            throw new IllegalArgumentException("El aeropuerto de origen y el de destino no pueden ser el mismo");
        }
        return dataScienceClient.llamarModelo(request);
    }
}
