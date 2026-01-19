package com.flightontime.flightontimeapi.exception;

import com.flightontime.flightontimeapi.dto.ErrorResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Resiliencia: Informar correctamente al usuario si el motor de IA está fuera de servicio (503)")
    void cuandoSeLanzaRemoteServiceException_debeRetornarStatus503() {
        RemoteServiceException ex = new RemoteServiceException("IA Service Unavailable");

        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleRemoteServiceError(ex);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, respuesta.getStatusCode());
    }

    @Test
    @DisplayName("Validación: Formato de fecha inválido (400) para días inexistentes como 31 de noviembre")
    void cuandoFechaEsInexistente_debeRetornarMensajePersonalizado() {
        String mensajeTecnico = "Cannot deserialize value of type java.time.LocalDateTime from String \"2026-11-31T00:12\"";
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(mensajeTecnico, null, null);
        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleInvalidFormat(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.hasBody());
        assertTrue(respuesta.getBody().getMessage().contains("Fecha inválida"));
    }

    @Test
    @DisplayName("Validación: Código de aerolínea debe ser de 2 caracteres (ej: IB)")
    void cuandoAerolineaEsInvalida_debeRetornarError400() {
         IllegalArgumentException ex = new IllegalArgumentException("El código de la aerolínea debe tener 2 caracteres.");
         ResponseEntity<ErrorResponseDTO> respuesta = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.hasBody());
        assertEquals("El código de la aerolínea debe tener 2 caracteres.", respuesta.getBody().getMessage());
    }

    @Test
    @DisplayName("Validación: Los aeropuertos de origen y destino deben ser diferentes")
    void cuandoAeropuertosSonIguales_debeRetornarError400() {
        IllegalArgumentException ex = new IllegalArgumentException("El aeropuerto de origen y destino no pueden ser el mismo.");
        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.hasBody());
        assertEquals("El aeropuerto de origen y destino no pueden ser el mismo.", respuesta.getBody().getMessage());
    }

    @Test
    @DisplayName("Validación: El formato de fecha debe ser ISO (yyyy-MM-dd'T'HH:mm)")
    void cuandoFormatoFechaEsTextoInvalido_debeRetornarErrorDeFormato() {
        // Simular que el usuario envió letras en lugar de una fecha
        String mensajeTecnico = "Cannot deserialize value of type java.time.LocalDateTime from String \"esto-no-es-una-fecha\"";
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(mensajeTecnico, null, null);
        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleInvalidFormat(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.hasBody());
        // Verificar que el sistema detecte el error de tipo LocalDateTime
        assertTrue(respuesta.getBody().getMessage().contains("Fecha inválida"));
    }

    @Test
    @DisplayName("Validación: La distancia debe ser un número (no permite letras)")
    void cuandoDistanciaTieneLetras_debeRetornarErrorDeTipo() {
        // Simular que enviaron "100km" en lugar de 100.0
        String mensajeTecnico = "Cannot deserialize value of type Double from String \"100km\"";
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(mensajeTecnico, null, null);
        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleInvalidFormat(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.hasBody());
        assertEquals("La distancia debe ser un valor numérico (ej: 1200.50). No se permiten letras.",
                respuesta.getBody().getMessage());
    }

    @Test
    @DisplayName("Validación: La fecha de partida no puede ser una fecha pasada")
    void cuandoFechaEsPasada_debeRetornarError400() {
        // Simular el mensaje que lanzaría el servicio de validación
        String mensajeEsperado = "La fecha de partida no puede estar en el pasado.";
        IllegalArgumentException ex = new IllegalArgumentException(mensajeEsperado);
        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.hasBody());
        assertEquals(mensajeEsperado, respuesta.getBody().getMessage());
    }

    @Test
    @DisplayName("Validación: El código IATA de aeropuerto debe tener exactamente 3 caracteres")
    void cuandoCodigoIataNoTiene3Caracteres_debeRetornarError400() {
        // Ejemplo: Enviaron "BUENOSAIRES" en lugar de "EZE"
        String mensajeEsperado = "El código de aeropuerto debe tener exactamente 3 caracteres (ej: MAD, EZE).";
        IllegalArgumentException ex = new IllegalArgumentException(mensajeEsperado);
        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.hasBody());
        assertEquals(mensajeEsperado, respuesta.getBody().getMessage());
    }

}