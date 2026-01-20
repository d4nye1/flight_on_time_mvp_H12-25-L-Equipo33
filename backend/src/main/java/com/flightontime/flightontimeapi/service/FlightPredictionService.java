package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.FlightPredictionDTO;
import com.flightontime.flightontimeapi.dto.FlightRequestDTO;
import com.flightontime.flightontimeapi.dto.FlightPredictionWithStatsDTO;
import com.flightontime.flightontimeapi.entity.Prediction;
import com.flightontime.flightontimeapi.repository.PredictionRepository;
import com.flightontime.flightontimeapi.exception.RemoteServiceException;
import com.flightontime.flightontimeapi.service.DataScienceClient;
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
      
      LocalDateTime fechaPartida = request.getFechaPartida().withSecond(0).withNano(0);

        return predictionRepository
                .findByAerolineaAndOrigenAndDestinoAndFechaPartida(
                        request.getAerolinea(), request.getOrigen(), request.getDestino(), fechaPartida)
                .map(this::mapToDTO)
                .orElseGet(() -> consultarYGuardar(request, fechaPartida));
    }

    @Transactional
    public FlightPredictionWithStatsDTO predecirVueloConStats(FlightRequestDTO request) {

      FlightPredictionDTO prediccion = predecirVuelo(request);
        if (prediccion == null) {
            throw new RemoteServiceException("No se pudo generar la predicción");
        }

        long totalVuelosRuta = predictionRepository.countTotalPorRuta(
                request.getAerolinea(), request.getOrigen(), request.getDestino(),
                LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));

        long vuelosRetrasadosRuta = predictionRepository.countRetrasadosPorRuta(
                request.getAerolinea(), request.getOrigen(), request.getDestino(),
                LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));

        double porcentajeRetrasos = totalVuelosRuta == 0 ? 0 : (vuelosRetrasadosRuta * 100.0 / totalVuelosRuta);

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

    private FlightPredictionDTO consultarYGuardar(FlightRequestDTO request, LocalDateTime fechaPartida) {
        try {
            FlightPredictionDTO respuesta = dataScienceClient.llamarModelo(request);

            Prediction pred = new Prediction();
            pred.setAerolinea(request.getAerolinea());
            pred.setOrigen(request.getOrigen());
            pred.setDestino(request.getDestino());
            pred.setFechaPartida(fechaPartida);
            pred.setPrevision(respuesta.getPrevision());
            pred.setProbabilidad(respuesta.getProbabilidad());
            pred.setDistancia(respuesta.getDistancia());
            pred.setFechaConsulta(LocalDateTime.now());

            predictionRepository.save(pred);
            return respuesta;

        } catch (Exception e) {
            manejarErrorIA(e);
            throw e; // NUNCA devolver null
        }
    }

    private void manejarErrorIA(Exception e) {
        String msg = (e.getMessage() != null) ? e.getMessage().toLowerCase() : "";
        if (msg.contains("timeout") || msg.contains("timed out")) {
            throw new RemoteServiceException("El motor de predicción está tardando demasiado. Intente nuevamente.");
        }
        throw new RemoteServiceException("El motor de predicción no responde. Intente en unos minutos.");
    }

    private FlightPredictionDTO mapToDTO(Prediction entity) {
        FlightPredictionDTO dto = new FlightPredictionDTO();
        dto.setPrevision(entity.getPrevision());
        dto.setProbabilidad(entity.getProbabilidad());
        dto.setDistancia(entity.getDistancia());
        return dto;
    }
}