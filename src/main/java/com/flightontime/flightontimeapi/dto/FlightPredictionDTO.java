package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightPredictionDTO {

    @JsonProperty("prevision")
    private String prevision;

    @JsonProperty("probabilidad")
    private Double probabilidad;

    //  Constructor vacío (Jackson)
    public FlightPredictionDTO() {
    }

    public FlightPredictionDTO(String prevision, Double probabilidad) {
        this.prevision = prevision;
        this.probabilidad = probabilidad;
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
}
