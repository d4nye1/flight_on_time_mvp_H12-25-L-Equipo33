package com.flightontime.flightontimeapi.exception;

import com.flightontime.flightontimeapi.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationError(MethodArgumentNotValidException ex) {
        String mensaje = "Error de validación";

        if (!ex.getBindingResult().getAllErrors().isEmpty()) {
            mensaje = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(mensaje));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(
            IllegalArgumentException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceUnavailable(
            IllegalStateException ex
    ) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericError() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        "Ocurrió un error inesperado en el sistema"
                ));
    }
}