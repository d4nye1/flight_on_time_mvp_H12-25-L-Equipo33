package com.flightontime.flightontimeapi.dto;

import java.util.List;

public class FlightPredictionWithStatsDTO {

    private String prevision;
    private double probabilidad;
    private long totalVuelosRuta;
    private long vuelosRetrasadosRuta;
    private double porcentajeRetrasosRuta;
    private String recomendacion;
    private double distancia;

    // --- NUEVOS CAMPOS PARA LA GRÁFICA REAL ---
    private List<Double> historialPuntualidad;
    private List<String> etiquetasFechas;

    public FlightPredictionWithStatsDTO() {
    }
    public FlightPredictionWithStatsDTO(String prevision, double probabilidad, long totalVuelosRuta,
                                        long vuelosRetrasadosRuta, double porcentajeRetrasosRuta,
                                        String recomendacion, double distancia,
                                        List<Double> historialPuntualidad, List<String> etiquetasFechas) {
        this.prevision = prevision;
        this.probabilidad = probabilidad;
        this.totalVuelosRuta = totalVuelosRuta;
        this.vuelosRetrasadosRuta = vuelosRetrasadosRuta;
        this.porcentajeRetrasosRuta = porcentajeRetrasosRuta;
        this.recomendacion = recomendacion;
        this.distancia = distancia;
        this.historialPuntualidad = historialPuntualidad;
        this.etiquetasFechas = etiquetasFechas;
    }

    // Getters existentes
    public String getPrevision() { return prevision; }
    public double getProbabilidad() { return probabilidad; }
    public long getTotalVuelosRuta() { return totalVuelosRuta; }
    public long getVuelosRetrasadosRuta() { return vuelosRetrasadosRuta; }
    public double getPorcentajeRetrasosRuta() { return porcentajeRetrasosRuta; }
    public String getRecomendacion() { return recomendacion; }
    public double getDistancia() { return distancia; }

    // --- NUEVOS GETTERS ---
    public List<Double> getHistorialPuntualidad() { return historialPuntualidad; }
    public List<String> getEtiquetasFechas() { return etiquetasFechas; }

    public void setPrevision(String prevision) {
        this.prevision = prevision;
    }

    public void setProbabilidad(double probabilidad) {
        this.probabilidad = probabilidad;
    }

    public void setTotalVuelosRuta(long totalVuelosRuta) {
        this.totalVuelosRuta = totalVuelosRuta;
    }

    public void setVuelosRetrasadosRuta(long vuelosRetrasadosRuta) {
        this.vuelosRetrasadosRuta = vuelosRetrasadosRuta;
    }

    public void setPorcentajeRetrasosRuta(double porcentajeRetrasosRuta) {
        this.porcentajeRetrasosRuta = porcentajeRetrasosRuta;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public void setHistorialPuntualidad(List<Double> historialPuntualidad) {
        this.historialPuntualidad = historialPuntualidad;
    }

    public void setEtiquetasFechas(List<String> etiquetasFechas) {
        this.etiquetasFechas = etiquetasFechas;
    }

}