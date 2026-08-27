package ru.practicum.ewm.server.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.server.event.model.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    long countByCategoryId(Long categoryId);

    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    @Query(
            value = """
                SELECT *
                FROM events e
                WHERE e.state = 'PUBLISHED'
                  AND e.lat IS NOT NULL
                  AND e.lon IS NOT NULL
                  AND 6371 * 2 * ASIN(
                        SQRT(
                            POWER(SIN(RADIANS(CAST(e.lat AS double precision) - :centerLat) / 2), 2) +
                            COS(RADIANS(:centerLat)) *
                            COS(RADIANS(CAST(e.lat AS double precision))) *
                            POWER(SIN(RADIANS(CAST(e.lon AS double precision) - :centerLon) / 2), 2)
                        )
                  ) <= CAST(:radiusM AS double precision) / 1000
                ORDER BY e.event_date ASC
                -- #pageable
                """,
            countQuery = """
                SELECT count(*)
                FROM events e
                WHERE e.state = 'PUBLISHED'
                  AND e.lat IS NOT NULL
                  AND e.lon IS NOT NULL
                  AND 6371 * 2 * ASIN(
                        SQRT(
                            POWER(SIN(RADIANS(CAST(e.lat AS double precision) - :centerLat) / 2), 2) +
                            COS(RADIANS(:centerLat)) *
                            COS(RADIANS(CAST(e.lat AS double precision))) *
                            POWER(SIN(RADIANS(CAST(e.lon AS double precision) - :centerLon) / 2), 2)
                        )
                  ) <= CAST(:radiusM AS double precision) / 1000
                """,
            nativeQuery = true
    )
    Page<Event> findPublishedInRadius(
            @Param("centerLat") double centerLat,
            @Param("centerLon") double centerLon,
            @Param("radiusM") int radiusM,
            Pageable pageable
    );
}
