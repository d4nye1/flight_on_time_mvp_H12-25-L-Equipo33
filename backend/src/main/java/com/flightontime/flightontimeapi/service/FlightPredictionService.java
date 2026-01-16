package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionWithStatsDTO;
import com.flightontime.flightontimeapi.entity.Prediction;
import com.flightontime.flightontimeapi.repository.PredictionRepository;
import com.flightontime.flightontimeapi.exception.RemoteServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightPredictionService {

    private final DataScienceClient dataScienceClient;
    private final PredictionRepository predictionRepository;

    public FlightPredictionService(DataScienceClient dataScienceClient, PredictionRepository predictionRepository) {
        this.dataScienceClient = dataScienceClient;
        this.predictionRepository = predictionRepository;
    }

    @Transactional
    public FlightPredictionDTO predecirVuelo(FlightRequestDTO request) {
        // 1. Llamada a FastAPI (Modelo Joblib)
        FlightPredictionDTO respuesta;
        try {
            respuesta = dataScienceClient.llamarModelo(request);
        } catch (Exception e) {
            throw new RemoteServiceException("El motor de predicción no responde: " + e.getMessage());
        }

        // 2. GUARDADO AUTOMÁTICO (Sin bloqueos de "existe")
        Prediction pred = new Prediction();
        pred.setAerolinea(request.getAerolinea());
        pred.setOrigen(request.getOrigen());
        pred.setDestino(request.getDestino());
        pred.setFechaPartida(request.getFechaPartida());
        pred.setPrevision(respuesta.getPrevision());
        pred.setProbabilidad(respuesta.getProbabilidad());
        pred.setDistancia(respuesta.getDistancia());
        pred.setFechaConsulta(LocalDateTime.now());

        predictionRepository.save(pred);

        return respuesta;
    }

    @Transactional
    public FlightPredictionWithStatsDTO predecirVueloConStats(FlightRequestDTO request) {
        // Ejecuta el guardado y la predicción
        FlightPredictionDTO prediccion = predecirVuelo(request);

        // Consultas de historial real en DB
        long totalVuelosRuta = predictionRepository.countTotalPorRuta(
                request.getAerolinea(), request.getOrigen(), request.getDestino(),
                LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));

        long vuelosRetrasadosRuta = predictionRepository.countRetrasadosPorRuta(
                request.getAerolinea(), request.getOrigen(), request.getDestino(),
                LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));

        double porcentajeRetrasos = totalVuelosRuta == 0 ? 0 : (vuelosRetrasadosRuta * 100.0 / totalVuelosRuta);

        // Lógica de gráfica LIMPIA
        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();

        List<Object[]> historialRaw = predictionRepository.findHistorialPuntualidadRuta(
                request.getAerolinea(), request.getOrigen(), request.getDestino());

        if (historialRaw != null && !historialRaw.isEmpty()) {
            for (Object[] fila : historialRaw) {
                etiquetas.add("Mes " + fila[0].toString());
                valores.add(((Number) fila[1]).doubleValue());
            }
        } else {
            // SI NO HAY HISTORIAL: Solo mostramos el punto actual de FastAPI
            etiquetas.add("Actual");
            valores.add(prediccion.getProbabilidad() * 100);
        }

        return new FlightPredictionWithStatsDTO(
                prediccion.getPrevision(),
                prediccion.getProbabilidad(),
                totalVuelosRuta,
                vuelosRetrasadosRuta,
                porcentajeRetrasos,
                totalVuelosRuta <= 1 ? "Primer registro en esta ruta" : "Basado en historial",
                prediccion.getDistancia(),
                valores,
                etiquetas
        );
    }
}