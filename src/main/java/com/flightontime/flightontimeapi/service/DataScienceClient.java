package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DataScienceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public FlightPredictionDTO llamarModelo(FlightRequestDTO request) {

        String url = "http://127.0.0.1:8000/predict";

        return restTemplate.postForObject(
                url,
                request,
                FlightPredictionDTO.class
        );
    }
}
