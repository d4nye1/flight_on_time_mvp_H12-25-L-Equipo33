package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataScienceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public FlightPredictionDTO llamarModelo(FlightRequestDTO request) {

        String url = "http://127.0.0.1:8000/predict";

        // 1️⃣ Construir JSON EXACTO que espera FastAPI
        Map<String, Object> body = new HashMap<>();
        body.put("aerolinea", request.getAerolinea());
        body.put("origen", request.getOrigen());
        body.put("destino", request.getDestino());
        body.put("fecha_partida",request.getFechaPartida().format(FORMATO_FECHA)
        );

        // 2️⃣ Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        try {
            // 3️⃣ Llamada al microservicio DS
            return restTemplate.postForObject(
                    url,
                    entity,
                    FlightPredictionDTO.class
            );
        } catch (RestClientException ex) {
            // 4️⃣ Error controlado → GlobalExceptionHandler
            throw new IllegalArgumentException(
                    "Error al consultar el servicio de predicción"
            );
        }
    }
}
