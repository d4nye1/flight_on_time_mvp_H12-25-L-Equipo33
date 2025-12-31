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
        return dataScienceClient.llamarModelo(request);
    }
}
