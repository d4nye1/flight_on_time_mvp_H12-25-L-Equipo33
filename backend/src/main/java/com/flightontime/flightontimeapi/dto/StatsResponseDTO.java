package com.flightontime.flightontimeapi.dto;

public class StatsResponseDTO {

    private String fecha;
    private long totalVuelos;
    private long vuelosRetrasados;
    private double porcentajeRetrasos;

    public StatsResponseDTO(
            String fecha,
            long totalVuelos,
            long vuelosRetrasados,
            double porcentajeRetrasos
    ) {
        this.fecha = fecha;
        this.totalVuelos = totalVuelos;
        this.vuelosRetrasados = vuelosRetrasados;
        this.porcentajeRetrasos = porcentajeRetrasos;
    }

    public String getFecha() {
        return fecha;
    }

    public long getTotalVuelos() {
        return totalVuelos;
    }

    public long getVuelosRetrasados() {
        return vuelosRetrasados;
    }

    public double getPorcentajeRetrasos() {
        return porcentajeRetrasos;
    }
}
