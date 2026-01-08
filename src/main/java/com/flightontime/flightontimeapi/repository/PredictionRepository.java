package com.flightontime.flightontimeapi.repository;

import com.flightontime.flightontimeapi.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    boolean existsByAerolineaAndOrigenAndDestinoAndFechaPartida(
            String aerolinea,
            String origen,
            String destino,
            LocalDateTime fechaPartida
    );

    Optional<Prediction> findByAerolineaAndOrigenAndDestinoAndFechaPartida(
            String aerolinea,
            String origen,
            String destino,
            LocalDateTime fechaPartida
    );
}
