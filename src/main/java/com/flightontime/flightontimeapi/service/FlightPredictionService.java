package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.entity.Prediction;
import com.flightontime.flightontimeapi.repository.PredictionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public FlightPredictionDTO predecirVuelo(FlightRequestDTO request) {

        LocalDateTime fechaPartida = request.getFechaPartida().withSecond(0).withNano(0);

        boolean existe = predictionRepository
                .existsByAerolineaAndOrigenAndDestinoAndFechaPartida(
                        request.getAerolinea(),
                        request.getOrigen(),
                        request.getDestino(),
                        fechaPartida
                );

        FlightPredictionDTO respuesta = dataScienceClient.llamarModelo(request);

        if (!existe) {
            Prediction pred = new Prediction();
            pred.setAerolinea(request.getAerolinea());
            pred.setOrigen(request.getOrigen());
            pred.setDestino(request.getDestino());
            pred.setFechaPartida(fechaPartida);
            pred.setPrevision(respuesta.getPrevision());
            pred.setProbabilidad(respuesta.getProbabilidad());
            pred.setDistancia(respuesta.getDistancia());
            pred.setFechaConsulta(LocalDateTime.now());

            try {
                predictionRepository.save(pred);
                System.out.println("Vuelo guardado correctamente.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El vuelo ya existe en la DB. No se guarda.");
        }
        return respuesta;
    }
}
