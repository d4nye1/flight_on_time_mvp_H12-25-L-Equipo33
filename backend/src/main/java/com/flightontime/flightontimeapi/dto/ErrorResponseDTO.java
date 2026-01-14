package com.flightontime.flightontimeapi.dto;

public class ErrorResponseDTO {

    private String status;
    private String message;

    public ErrorResponseDTO(String message) {
        this.status = "ERROR";
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
