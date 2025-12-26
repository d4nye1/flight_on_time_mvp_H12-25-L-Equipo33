package com.flightontime.flightontimeapi.dto;

public class FlightPredictionDTO {

    private String prevision;
    private Double probabilidad;

    public FlightPredictionDTO(String prevision, Double probabilidad) {
        this.prevision = prevision;
        this.probabilidad = probabilidad;
    }

    public String getPrevision() {
        return prevision;
    }

    public Double getProbabilidad() {
        return probabilidad;
    }
}
