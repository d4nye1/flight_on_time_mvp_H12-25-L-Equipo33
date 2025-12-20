package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class FlightRequestDTO {

    @NotBlank(message = "Campo 'aerolinea' es obligatorio")
    @Size(min = 2, max = 2, message = "El código de aerolínea debe tener exactamente 2 letras (IATA)")
    private String aerolinea;

    @NotBlank(message = "Campo 'origen' es obligatorio")
    @Size(min = 3, max = 3, message = "El código de origen debe tener exactamente 3 letras (IATA)")
    private String origen;

    @NotBlank(message = "Campo 'destino' es obligatorio")
    @Size(min = 3, max = 3, message = "El código de destino debe tener exactamente 3 letras (IATA)")
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
