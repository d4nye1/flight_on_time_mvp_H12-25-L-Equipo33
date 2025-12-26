package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class FlightRequestDTO {

    @NotBlank(message = "Asegúrate de completar todos los campos")
    @Size(min = 2, max = 2, message = "Dato incorrecto agregue las 2 letras del código de aerolínea en formato IATA")
    private String aerolinea;

    @NotBlank(message = "Asegúrate de completar todos los campos")
    @Size(min = 3, max = 3, message = "Dato incorrecto agregue las 3 letras del aeropuerto origen en formato IATA")
    private String origen;

    @NotBlank(message = "Asegúrate de completar todos los campos")
    @Size(min = 3, max = 3, message = "Dato incorrecto agregue las 3 letras del aeropuerto destino en formato IATA")
    private String destino;

    @NotNull(message = "Asegúrate de completar todos los campos")
    @Future(message = "Fecha inválida: No se pueden realizar predicciones para vuelos que ya han ocurrido")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("fecha_partida")
    private LocalDateTime fechaPartida;

    @NotNull(message = "Asegúrate de completar todos los campos")
    @Positive(message = "La distancia debe ser un valor positivo")
    @JsonProperty("distancia_km")
    private Integer distanciaKm;

    // Getters y setters

    public String getAerolinea() {
        return aerolinea;
    }

    public void setAerolinea(String aerolinea) {
        this.aerolinea = aerolinea;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDateTime getFechaPartida() {
        return fechaPartida;
    }

    public void setFechaPartida(LocalDateTime fechaPartida) {
        this.fechaPartida = fechaPartida;
    }

    public Integer getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Integer distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
}
