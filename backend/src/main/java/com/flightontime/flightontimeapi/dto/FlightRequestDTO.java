package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;


import java.time.LocalDateTime;

public class FlightRequestDTO {

    @NotBlank(message = "La aerolínea es obligatoria", groups = ValidacionGrupos.Primero.class)
    @Pattern(regexp = "^[A-Z0-9]{2}$", message = "La aerolínea debe ser un código IATA de 2 caracteres (ej: AA, 2G, A9)", groups = ValidacionGrupos.Segundo.class)
    @JsonProperty("aerolinea")
    private String aerolinea;

    @NotBlank(message = "El origen es obligatorio", groups = ValidacionGrupos.Primero.class)
    @Pattern(regexp = "^[A-Z]{3}$", message = "El origen debe ser un código IATA de 3 letras (ej: JFK, BOG)", groups = ValidacionGrupos.Segundo.class)
    @JsonProperty("origen")
    private String origen;

    @NotBlank(message = "El destino es obligatorio", groups = ValidacionGrupos.Primero.class)
    @Pattern(regexp = "^[A-Z]{3}$", message = "El destino debe ser un código IATA de 3 letras (ej: MIA, MDE)", groups = ValidacionGrupos.Segundo.class)
    @JsonProperty("destino")
    private String destino;

    @NotNull(message = "La fecha de partida es obligatoria", groups = ValidacionGrupos.Primero.class)
    @Future(message = "Fecha inválida: No se pueden realizar predicciones para vuelos pasados", groups = ValidacionGrupos.Segundo.class)
    @JsonProperty("fecha_partida")
    private LocalDateTime fechaPartida;

    @JsonProperty("distancia")
    @Positive(message = "La distancia debe ser un número mayor a cero", groups = ValidacionGrupos.Segundo.class)
    @Digits(integer = 5, fraction = 2, message = "La distancia tiene un formato numérico inválido (máximo 5 enteros y 2 decimales)", groups = ValidacionGrupos.Segundo.class)
    private Double distancia;

    @AssertTrue(message = "El aeropuerto de origen y destino no pueden ser iguales", groups = ValidacionGrupos.Segundo.class)
    @JsonIgnore
    public boolean isDestinoDiferenteOrigen() {
        if (origen == null || destino == null) return true;
        return !origen.trim().equalsIgnoreCase(destino.trim());
    }

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

    public Double getDistancia() { return distancia; }

    public void setDistancia(Double distancia) { this.distancia = distancia; }
}