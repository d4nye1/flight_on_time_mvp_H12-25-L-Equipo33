package com.flightontime.flightontimeapi.repository;

import com.flightontime.flightontimeapi.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Optional<Prediction> findByAerolineaAndOrigenAndDestinoAndFechaPartida(
            String aerolinea,
            String origen,
            String destino,
            LocalDateTime fechaPartida
    );

    // 🔹 Contar todos los vuelos por fechaPartida
    long countByFechaPartidaBetween(LocalDateTime inicio, LocalDateTime fin);

    // 🔹 Contar vuelos retrasados por fechaPartida
    @Query("""
        SELECT COUNT(p)
        FROM Prediction p
        WHERE p.prevision = 'Retrasado'
        AND p.fechaPartida BETWEEN :inicio AND :fin
    """)
    long countRetrasadosByFechaPartidaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("""
    SELECT COUNT(p)
    FROM Prediction p
    WHERE p.prevision = 'Retrasado'
    AND p.aerolinea = :aerolinea
    AND p.origen = :origen
    AND p.destino = :destino
    AND p.fechaPartida BETWEEN :inicio AND :fin
""")
    long countRetrasadosPorRuta(
            String aerolinea,
            String origen,
            String destino,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    @Query("""
    SELECT COUNT(p)
    FROM Prediction p
    WHERE p.aerolinea = :aerolinea
    AND p.origen = :origen
    AND p.destino = :destino
    AND p.fechaPartida BETWEEN :inicio AND :fin
""")
    long countTotalPorRuta(
            String aerolinea,
            String origen,
            String destino,
            LocalDateTime inicio,
            LocalDateTime fin
    );
    @Query(value = """
    SELECT (ORIGEN || '-' || DESTINO) as ruta, COUNT(*) as total 
    FROM PREDICTIONS 
    WHERE PREVISION IN ('Retrasado', 'Retraso') 
    GROUP BY ORIGEN, DESTINO 
    ORDER BY total DESC 
    LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findTop5RutasConRetrasos();

    @Query(value = """
    SELECT mes, AVG(promedio_retraso) as promedio
    FROM (
        SELECT
            CAST(EXTRACT(MONTH FROM fecha_partida) AS TEXT) as mes,
            CASE
                WHEN prevision = 'Retrasado' THEN 100.0
                ELSE 0.0
            END as promedio_retraso
        FROM predictions
        WHERE aerolinea = :aerolinea
          AND origen = :origen
          AND destino = :destino
    ) subquery
    GROUP BY mes
    ORDER BY mes ASC
    LIMIT 4
""", nativeQuery = true)
    List<Object[]> findHistorialPuntualidadRuta(
            @Param("aerolinea") String aerolinea,
            @Param("origen") String origen,
            @Param("destino") String destino
    );

}