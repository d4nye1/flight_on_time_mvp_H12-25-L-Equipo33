package com.flightontime.flightontimeapi.exception;

import com.flightontime.flightontimeapi.entity.Prediction;
import com.flightontime.flightontimeapi.repository.PredictionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest // Usa una base de datos en memoria (H2), ultra rápida
class PredictionRepositoryTest {

    @Autowired
    private PredictionRepository repository;

    @Test
    @DisplayName("💾 Repositorio: Verificar que se guardan y recuperan predicciones correctamente")
    void cuandoGuardaPrediccion_debeEncontrarlaPorCamposClave() {
        // 1. Preparamos una entidad real
        LocalDateTime fecha = LocalDateTime.now().withSecond(0).withNano(0);
        Prediction pred = new Prediction();
        pred.setAerolinea("AA");
        pred.setOrigen("EZE");
        pred.setDestino("MAD");
        pred.setFechaPartida(fecha);
        pred.setPrevision("A Tiempo");
        pred.setProbabilidad(0.95);

        // 2. Guardamos
        repository.save(pred);

        // 3. Buscamos (como lo hace tu Service)
        Optional<Prediction> encontrada = repository
                .findByAerolineaAndOrigenAndDestinoAndFechaPartida("AA", "EZE", "MAD", fecha);

        // 4. Verificamos el éxito
        assertTrue(encontrada.isPresent());
        assertEquals("A Tiempo", encontrada.get().getPrevision());
    }
}
