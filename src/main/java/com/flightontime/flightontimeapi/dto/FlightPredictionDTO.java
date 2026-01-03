package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightPredictionDTO {

    @JsonProperty("prevision")
    private String prevision;

    @JsonProperty("probabilidad")
    private Double probabilidad;

    @JsonProperty("distancia") // ✅ Nuevo campo añadido
    private Double distancia;

    public FlightPredictionDTO() {
    }

    public FlightPredictionDTO(String prevision, Double probabilidad, Double distancia) {
        this.prevision = prevision;
        this.probabilidad = probabilidad;
        this.distancia = distancia;
    }

    public String getPrevision() {
        return prevision;
    }

    public void setPrevision(String prevision) {
        this.prevision = prevision;
    }

    public Double getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(Double probabilidad) {
        this.probabilidad = probabilidad;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
}