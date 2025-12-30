package com.flightontime.flightontimeapi.dto;

public class ErrorResponseDTO {

    private String error;
    private Integer codigo;

    public ErrorResponseDTO(String error, Integer codigo) {
        this.error = error;
        this.codigo = codigo;
    }

    public String getError() {
        return error;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
