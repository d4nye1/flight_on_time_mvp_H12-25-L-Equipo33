package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class FlightRequestDTO {

    @NotBlank(message = "Asegúrate de incluir el código de la aerolínea")
    @Size(min = 2, max = 2, message = "Formato inválido: El código de aerolínea debe tener exactamente 2 letras mayúsculas (IATA)")
    private String aerolinea;

    @NotBlank(message = "Asegúrate de incluir el código del aeropuerto de origen")
    @Size(min = 3, max = 3, message = "Formato inválido: El código de aeropuerto debe tener exactamente 3 letras mayúsculas (IATA)")
    private String origen;

    @NotBlank(message = "Asegúrate de incluir el código del aeropuerto de destino")
    @Size(min = 3, max = 3, message = "Formato inválido: El código de aeropuerto debe tener exactamente 3 letras mayúsculas (IATA)")
    private String destino;

    @NotNull(message = "Asegúrate de incluir la fecha de vuelo")
    @Future(message = "Fecha inválida: No se pueden realizar predicciones para vuelos que ya han ocurrido")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("fecha_partida")
    private LocalDateTime fechaPartida;

    @NotNull(message = "Asegúrate de incluir la distancia en km")
    @Positive(message = "La distancia debe ser un valor positivo y coherente con el trayecto")
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
