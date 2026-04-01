package ru.practicum.ewm.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
                SELECT new ru.practicum.ewm.dto.ViewStats(
                       eh.app,
                       eh.uri,
                       CASE WHEN :unique = true
                            THEN COUNT(DISTINCT eh.ip)
                            ELSE COUNT(eh) END)
                FROM EndpointHit eh
                WHERE eh.timestamp BETWEEN :start AND :end
                  AND (:uri IS NULL OR eh.uri IN :uri)
                GROUP BY eh.app, eh.uri
                ORDER BY COUNT(eh) DESC
            """)
    List<ViewStats> getAllViewStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                    @Param("uri") List<String> uri, @Param("unique") Boolean unique);

}