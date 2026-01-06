package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.entity.Prediction;
import com.flightontime.flightontimeapi.repository.PredictionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FlightPredictionService {

    private final DataScienceClient dataScienceClient;
    private final PredictionRepository predictionRepository;

    public FlightPredictionService(
            DataScienceClient dataScienceClient,
            PredictionRepository predictionRepository
    ) {
        this.dataScienceClient = dataScienceClient;
        this.predictionRepository = predictionRepository;
    }

    public FlightPredictionDTO predecirVuelo(FlightRequestDTO request) {

        FlightPredictionDTO respuesta = dataScienceClient.llamarModelo(request);

        Prediction pred = new Prediction();
        pred.setAerolinea(request.getAerolinea());
        pred.setOrigen(request.getOrigen());
        pred.setDestino(request.getDestino());
        pred.setPrevision(respuesta.getPrevision());
        pred.setProbabilidad(respuesta.getProbabilidad());
        pred.setDistancia(respuesta.getDistancia());
        pred.setEstado("EXITOSA");
        pred.setFechaConsulta(LocalDateTime.now());

        predictionRepository.save(pred);

        return respuesta;
    }
}
