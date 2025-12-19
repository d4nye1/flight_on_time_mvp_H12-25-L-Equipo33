package com.flightontime.flightontimeapi.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/predict")
public class PredictController {

    @PostMapping
    public Map<String, Object> predict(@RequestBody Map<String, Object> vuelo) {
        // Creamos el mapa para la respuesta
        Map<String, Object> respuesta = new HashMap<>();

        // Validación de campos obligatorios
        if (!vuelo.containsKey("aerolinea") ||
                !vuelo.containsKey("origen") ||
                !vuelo.containsKey("destino") ||
                !vuelo.containsKey("fecha_partida") ||
                !vuelo.containsKey("distancia_km")) {

            respuesta.put("error", "Faltan campos obligatorios");
            return respuesta;
        }

        // Respuesta fija de ejemplo (más adelante se pondrá el modelo real)
        respuesta.put("prevision", "Retrasado");
        respuesta.put("probabilidad", 0.78);

        return respuesta;
    }
}
