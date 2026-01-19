package com.flightontime.flightontimeapi.controller;

import com.flightontime.flightontimeapi.dto.StatsResponseDTO;
import com.flightontime.flightontimeapi.service.StatsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*") // Importante para que Streamlit/HTML no de error de CORS
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // Esta es la URL que usará tu Dashboard para el resumen: GET /api/stats/summary
    @GetMapping("/summary")
    public StatsResponseDTO obtenerResumen() {
        return statsService.obtenerEstadisticasDelDia();
    }

    // Esta es la URL para gráficas: GET /api/stats/historico
    @GetMapping("/historico")
    public List<StatsResponseDTO> obtenerHistorico(
            @RequestParam(required = false) String fecha_inicio,
            @RequestParam(required = false) String fecha_fin
    ) {
        return statsService.obtenerHistorico(fecha_inicio, fecha_fin);
    }
    @GetMapping("/top-rutas")
    public List<Map<String, Object>> obtenerTopRutas() {
        return statsService.obtenerTopRutas();
    }
}