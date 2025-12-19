package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class FlightRequestDTO {

    @NotBlank(message = "Campo 'aerolinea' es obligatorio")
    private String aerolinea;

    @NotBlank(message = "Campo 'origen' es obligatorio")
    private String origen;

    @NotBlank(message = "Campo 'destino' es obligatorio")
    private String destino;

    @NotNull(message = "Campo 'fecha_partida' es obligatorio")
    @JsonProperty("fecha_partida")
    private LocalDateTime fechaPartida;

    @NotNull(message = "Campo 'distancia_km' es obligatorio")
    @Positive(message = "Campo 'distancia_km' debe ser positivo")
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
