package com.flightontime.flightontimeapi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest // Levanta todo el contexto de Spring (Simula la App corriendo)
@AutoConfigureMockMvc // Activa el "Postman" interno para los tests
public class FlightIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Integración: Validar rechazo de fecha inexistente (31 de Noviembre)")
    void cuandoFechaEsInvalida_debeResponder400YMensajeClaro() throws Exception {
         String jsonPayload = """
            {
                "aerolinea": "IB",
                "origen": "EZE",
                "destino": "MAD",
                "fecha_partida": "2026-11-31T10:00",
                "distancia": 100,
            }
            """;
        mockMvc.perform(post("/api/flights/predict") // Cambia la URL por tu endpoint real
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest()) // Verifica que el status sea 400
                .andExpect(jsonPath("$.message").value(containsString("Fecha inválida")));
    }

    @Test
    @DisplayName("Integración: Validar error cuando el código IATA es demasiado largo")
    void cuandoIataEsLargo_debeResponder400() throws Exception {
        String jsonPayload = """
            {
                "aerolinea": "IB",
                "origen": "BUENOSAIRES",
                "destino": "MAD",
                "fecha_partida": "2026-10-01T10:00",
                "distancia": 100,
            }
            """;
        mockMvc.perform(post("/api/flights/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integración: Validar rechazo de origen y destino iguales")
    void cuandoOrigenYDestinoSonIguales_debeResponder400() throws Exception {
         String jsonPayload = """
            {
                "aerolinea": "AA",
                "origen": "EZE",
                "destino": "EZE",
                "fecha_partida": "2026-12-01T10:00",
                "distancia": 100.0
            }
            """;
        mockMvc.perform(post("/api/flights/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("iguales")));
    }

    @Test
    @DisplayName("Integración: Validar que la distancia no sea negativa")
    void cuandoDistanciaEsNegativa_debeResponder400() throws Exception {
        String jsonPayload = """
        {
            "aerolinea": "AA",
            "origen": "EZE",
            "destino": "JFK",
            "fecha_partida": "2026-12-01T10:00",
            "distancia": -500.0
        }
        """;
        mockMvc.perform(post("/api/flights/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("distancia")));
    }

}
