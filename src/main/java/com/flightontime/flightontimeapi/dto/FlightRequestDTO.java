package com.flightontime.flightontimeapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class FlightRequestDTO {

    @NotBlank(message = "Asegúrate de incluir el código de la aerolínea")
    @Pattern(
            regexp = "^[A-Z0-9]{2}$",
            message = "Formato inválido: El código de aerolínea debe tener 2 caracteres alfanuméricos en mayúsculas (IATA)"
    )
    private String aerolinea;

    @NotBlank(message = "Asegúrate de incluir el código del aeropuerto de origen")
    @Size(min = 3, max = 3, message = "Formato inválido: El código de aeropuerto debe tener exactamente 3 letras mayúsculas (IATA)")
    private String origen;

    @NotBlank(message = "Asegúrate de incluir el código del aeropuerto de destino")
    @Size(min = 3, max = 3, message = "Formato inválido: El código de aeropuerto debe tener exactamente 3 letras mayúsculas (IATA)")
    private String destino;

    @NotBlank(message = "Asegúrate de incluir la fecha de vuelo")
    @JsonProperty("fecha_partida")
    private String fecha_partida;

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

    public String getFecha_partida() {
        return fecha_partida;
    }

    public void setFecha_partida(String fecha_partida) {
        this.fecha_partida = fecha_partida;
    }
}
