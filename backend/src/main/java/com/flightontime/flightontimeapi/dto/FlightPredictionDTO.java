package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightPredictionDTO {

    @JsonProperty("prevision")
    private String prevision;

    @JsonProperty("probabilidad")
    private Double probabilidad;

    @JsonProperty("distancia")
    private Double distancia;

    @JsonProperty("explicabilidad")
    private String explicabilidad;

    public FlightPredictionDTO() {
    }

    public FlightPredictionDTO(String prevision, Double probabilidad, Double distancia, String explicabilidad) {
        this.prevision = prevision;
        this.probabilidad = probabilidad;
        this.distancia = distancia;
        this.explicabilidad = explicabilidad;
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

    public String getExplicabilidad() {
        return explicabilidad;
    }

    public void setExplicabilidad(String explicabilidad) {
        this.explicabilidad = explicabilidad;
    }
}