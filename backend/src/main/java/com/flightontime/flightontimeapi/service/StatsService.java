package com.flightontime.flightontimeapi.service;

import com.flightontime.flightontimeapi.dto.StatsResponseDTO;
import com.flightontime.flightontimeapi.repository.PredictionRepository;
import com.flightontime.flightontimeapi.dto.FlightPredictionWithStatsDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

@Service
public class StatsService {

    private final PredictionRepository predictionRepository;

    public StatsService(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    public StatsResponseDTO obtenerEstadisticasDelDia() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.now().plusMonths(6);

        long total = predictionRepository.countByFechaPartidaBetween(inicio, fin);
        long retrasados = predictionRepository.countRetrasadosByFechaPartidaBetween(inicio, fin);

        double porcentaje = total == 0 ? 0 : (retrasados * 100.0 / total);

        return new StatsResponseDTO(
                "Panel de Control Global",
                total,
                retrasados,
                porcentaje
        );
    }

    public FlightPredictionWithStatsDTO obtenerPrediccionConStats(
            String aerolinea, String origen, String destino, LocalDateTime fechaPartida
    ) {
        LocalDateTime inicioHist = fechaPartida.minusDays(30);
        LocalDateTime finHist = fechaPartida.minusSeconds(1);

        long totalVuelosRuta = predictionRepository.countTotalPorRuta(aerolinea, origen, destino, inicioHist, finHist);
        long vuelosRetrasadosRuta = predictionRepository.countRetrasadosPorRuta(aerolinea, origen, destino, inicioHist, finHist);

        double porcentajeRetrasosRuta = totalVuelosRuta == 0 ? 0 : (vuelosRetrasadosRuta * 100.0 / totalVuelosRuta);
        return new FlightPredictionWithStatsDTO(
                "Procesado",
                0.0,
                totalVuelosRuta,
                vuelosRetrasadosRuta,
                porcentajeRetrasosRuta,
                totalVuelosRuta == 0 ? "Sin datos históricos" : "Basado en historial de 30 días",
                0.0,
                java.util.List.of(),
                java.util.List.of(),
                "Análisis de historial completado"
        );
    }

    public List<StatsResponseDTO> obtenerHistorico(String fechaInicioStr, String fechaFinStr) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio = (fechaInicioStr == null || fechaInicioStr.isEmpty()) ? hoy.minusDays(30) : LocalDate.parse(fechaInicioStr);
        LocalDate fechaFin = (fechaFinStr == null || fechaFinStr.isEmpty()) ? hoy : LocalDate.parse(fechaFinStr);

        List<StatsResponseDTO> historico = new ArrayList<>();
        LocalDate current = fechaInicio;

        while (!current.isAfter(fechaFin)) {
            LocalDateTime inicioDia = current.atStartOfDay();
            LocalDateTime finDia = current.atTime(LocalTime.MAX);

            long total = predictionRepository.countByFechaPartidaBetween(inicioDia, finDia);
            long retrasados = predictionRepository.countRetrasadosByFechaPartidaBetween(inicioDia, finDia);
            double porcentaje = total == 0 ? 0 : (retrasados * 100.0 / total);

            historico.add(new StatsResponseDTO(current.toString(), total, retrasados, porcentaje));
            current = current.plusDays(1);
        }
        return historico;
    }

    public List<Map<String, Object>> obtenerTopRutas() {
        List<Object[]> resultados = predictionRepository.findTop5RutasConRetrasos();
        List<Map<String, Object>> topRutas = new ArrayList<>();

        for (Object[] fila : resultados) {
            Map<String, Object> ruta = new HashMap<>();
            ruta.put("nombre", fila[0] != null ? fila[0].toString() : "N/A");
            ruta.put("cantidad", fila[1] != null ? ((Number) fila[1]).intValue() : 0);
            topRutas.add(ruta);
        }
        return topRutas;
    }
}