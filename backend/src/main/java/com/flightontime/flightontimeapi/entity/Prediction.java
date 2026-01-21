package com.flightontime.flightontimeapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions", indexes = {
        @Index(name = "idx_flight_cache", columnList = "aerolinea, origen, destino, fechaPartida")
})
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aerolinea;
    private String origen;
    private String destino;
    private String prevision;
    private Double probabilidad;
    private Double distancia;

    @Column(columnDefinition = "TEXT") // Permite explicaciones largas
    private String explicabilidad;

    private LocalDateTime fechaPartida;
    private LocalDateTime fechaConsulta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAerolinea() { return aerolinea; }
    public void setAerolinea(String aerolinea) { this.aerolinea = aerolinea; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getPrevision() { return prevision; }
    public void setPrevision(String prevision) { this.prevision = prevision; }

    public Double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(Double probabilidad) { this.probabilidad = probabilidad; }

    public Double getDistancia() { return distancia; }
    public void setDistancia(Double distancia) { this.distancia = distancia; }

    public String getExplicabilidad() { return explicabilidad; }
    public void setExplicabilidad(String explicabilidad) { this.explicabilidad = explicabilidad; }

    public LocalDateTime getFechaPartida() { return fechaPartida; }
    public void setFechaPartida(LocalDateTime fechaPartida) { this.fechaPartida = fechaPartida; }

    public LocalDateTime getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDateTime fechaConsulta) { this.fechaConsulta = fechaConsulta; }
}