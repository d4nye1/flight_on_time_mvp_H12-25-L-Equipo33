package com.flightontime.flightontimeapi.repository;

import com.flightontime.flightontimeapi.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Optional<Prediction> findByAerolineaAndOrigenAndDestinoAndFechaPartida(
            String aerolinea,
            String origen,
            String destino,
            LocalDateTime fechaPartida
    );
}
