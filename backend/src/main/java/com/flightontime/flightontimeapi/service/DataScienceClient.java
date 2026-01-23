package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataScienceClient {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public DataScienceClient() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    public FlightPredictionDTO llamarModelo(FlightRequestDTO request) {

        Map<String, Object> body = new HashMap<>();
        body.put("aerolinea", request.getAerolinea().trim().toUpperCase());
        body.put("origen", request.getOrigen().trim().toUpperCase());
        body.put("destino", request.getDestino().trim().toUpperCase());
        body.put("fecha_partida", request.getFechaPartida().format(FORMATO_FECHA));
        body.put("distancia", request.getDistancia());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        String url = aiServiceUrl + "/predict";

        return restTemplate.postForObject(
                url,
                entity,
                FlightPredictionDTO.class
        );
    }

}