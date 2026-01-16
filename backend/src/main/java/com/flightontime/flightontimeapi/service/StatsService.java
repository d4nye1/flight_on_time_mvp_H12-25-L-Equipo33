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

    // ✅ Estadísticas del último día (Para que el Dashboard siempre tenga datos)
    public StatsResponseDTO obtenerEstadisticasDelDia() {
        // Rango amplio para asegurar datos en la demo
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

        // ✅ CORRECCIÓN: Agregamos el 7mo parámetro (0.0 para distancia) para que coincida con el DTO
        // Debes agregar List.of(), List.of() al final para completar los 9 parámetros
        return new FlightPredictionWithStatsDTO(
                "Procesado",
                0.0,
                totalVuelosRuta,
                vuelosRetrasadosRuta,
                porcentajeRetrasosRuta,
                totalVuelosRuta == 0 ? "Sin datos históricos" : "Basado en historial de 30 días",
                0.0,          // Parámetro 7: Distancia
                java.util.List.of(), // Parámetro 8: Historial vacío (NUEVO)
                java.util.List.of()  // Parámetro 9: Etiquetas vacías (NUEVO)
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
            // fila[0] es la ruta (Ej: JFK-LAX), fila[1] es el conteo
            ruta.put("nombre", fila[0] != null ? fila[0].toString() : "N/A");
            ruta.put("cantidad", fila[1] != null ? ((Number) fila[1]).intValue() : 0);
            topRutas.add(ruta);
        }
        return topRutas;
    }
}