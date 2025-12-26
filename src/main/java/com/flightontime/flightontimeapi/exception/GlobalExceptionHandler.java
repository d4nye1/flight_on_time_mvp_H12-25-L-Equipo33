package com.flightontime.flightontimeapi.exception;

import com.flightontime.flightontimeapi.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1️⃣ VALIDACIÓN DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationError(
            MethodArgumentNotValidException ex
    ) {
        // 1. Recolectar todos los mensajes de error de los campos
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        // 2. Devolver el DTO con la lista completa de problemas
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(mensaje, 400));
    }

    // 2️⃣ ERRORES DE ENTRADA MANUALES
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(
            IllegalArgumentException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage(), 400));
    }

    // 3️⃣ ERRORES GENERALES (AL FINAL)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericError(
            Exception ex
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        "Modelo de predicción no disponible", 500));
    }
}
