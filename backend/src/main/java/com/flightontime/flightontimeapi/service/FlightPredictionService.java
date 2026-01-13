package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.entity.Prediction;
import com.flightontime.flightontimeapi.repository.PredictionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import com.flightontime.flightontimeapi.exception.RemoteServiceException;

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

        /*FlightPredictionDTO respuesta = dataScienceClient.llamarModelo(request);*/
        FlightPredictionDTO respuesta;
        try {
            respuesta = dataScienceClient.llamarModelo(request);
        } catch (Exception e) {
            String mensajeError = (e.getMessage() != null) ? e.getMessage().toLowerCase() : "";
            System.out.println("Error detectado: " + mensajeError); // Para que lo veas en consola
            if (mensajeError.contains("timeout") || mensajeError.contains("timed out")) {
                throw new RemoteServiceException("El motor de predicción está tardando demasiado en responder. Por favor, intente nuevamente.");
            }

            // Si no es un problema de tiempo, asumimos que el servicio está fuera de línea
            throw new RemoteServiceException("El motor de predicción no responde. Por favor, intente de nuevo en unos minutos.");
        }

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
