package com.flightontime.flightontimeapi.repository;

import com.flightontime.flightontimeapi.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
}
