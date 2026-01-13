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

        if (request.getAerolinea() == null || request.getAerolinea().isBlank()
                || request.getOrigen() == null || request.getOrigen().isBlank()
                || request.getDestino() == null || request.getDestino().isBlank()
                || request.getFechaPartida() == null) {
            throw new IllegalArgumentException("Asegúrate de completar todos los campos");
        }

        String url = "http://127.0.0.1:8000/predict";

        Map<String, Object> body = new HashMap<>();
        body.put("aerolinea", request.getAerolinea().trim().toUpperCase());
        body.put("origen", request.getOrigen().trim().toUpperCase());
        body.put("destino", request.getDestino().trim().toUpperCase());
        body.put("fecha_partida", request.getFechaPartida().format(FORMATO_FECHA));
        body.put("distancia", request.getDistancia());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            return restTemplate.postForObject(
                    url,
                    entity,
                    FlightPredictionDTO.class
            );

        } catch (RestClientException ex) {
            throw new IllegalStateException("Servicio de predicción no disponible: " + ex.getMessage());
        }
    }
}
