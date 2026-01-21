package com.flightontime.flightontimeapi.exception;

import com.flightontime.flightontimeapi.dto.ErrorResponseDTO;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Imports fundamentales para el manejo de colecciones de errores
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de formato (ej: enviar letras en campos numéricos o fechas inexistentes)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidFormat(HttpMessageNotReadableException ex) {
        String mensaje = "Error en el formato de la solicitud. Verifique los datos ingresados.";
        String technicalMessage = ex.getMessage() != null ? ex.getMessage() : "";

        if (technicalMessage.contains("java.time.LocalDateTime")) {
            mensaje = "Fecha inválida: El día ingresado no existe o el formato es incorrecto (yyyy-MM-dd'T'HH:mm).";
        } else if (technicalMessage.contains("Double") || technicalMessage.contains("double")) {
            mensaje = "La distancia debe ser un valor numérico (ej: 1200.50). No se permiten letras.";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(mensaje));
    }

    /**
     * Maneja las validaciones de los DTO (@NotBlank, @Pattern, @Future, etc.)
     * Devuelve un Mapa para que el Dashboard pueda pintar los bordes rojos en campos específicos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // Clave: nombre del campo (ej: "aerolinea"), Valor: mensaje de error
            errores.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceUnavailable(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleRemoteServiceError(RemoteServiceException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessValidation(ValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    /**
     * Captura cualquier otra excepción no controlada para evitar exponer detalles internos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericError(Exception ex) {
        // Logueamos el error para el desarrollador, pero enviamos mensaje genérico al cliente
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Ocurrió un error inesperado en el sistema"));
    }
}