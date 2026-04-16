package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.State;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
                SELECT e FROM Event e
                WHERE (:users IS NULL OR e.initiator.id IN :users)
                  AND (:states IS NULL OR e.state IN :states)
                  AND (:categories IS NULL OR e.category.id IN :categories)
                  AND (coalesce(:rangeStart, e.eventDate) <= e.eventDate)
                  AND (coalesce(:rangeEnd, e.eventDate) >= e.eventDate)
            """)
    Page<Event> findEventsByAdminFilters(
            @Param("users") List<Long> users,
            @Param("states") List<State> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    @Query("""
                SELECT e FROM Event e
                WHERE e.state = 'PUBLISHED'
                  AND (
                  :text IS NULL
                  OR LOWER(CAST(e.annotation AS text)) LIKE LOWER(CONCAT('%', :text, '%'))
                  OR LOWER(CAST(e.description AS text)) LIKE LOWER(CONCAT('%', :text, '%'))
                  )
                  AND (:categories IS NULL OR e.category.id IN :categories)
                  AND (:paid IS NULL OR e.paid = :paid)
                  AND (e.eventDate >= :rangeStart)
                  AND (e.eventDate <= :rangeEnd)
                  AND (:onlyAvailable = false OR e.confirmedRequests < e.participantLimit)
            """)
    Page<Event> searchPublicEventsAllParam(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable
    );

    @Query("""
                SELECT e FROM Event e
                WHERE (e.state = 'PUBLISHED')
                  AND (:categories IS NULL OR e.category.id IN :categories)
                  AND (:paid IS NULL OR e.paid = :paid)
                  AND (:onlyAvailable = false OR e.confirmedRequests < e.participantLimit)
            """)
    Page<Event> searchPublicEvents(
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable
    );

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    boolean existsByCategoryId(Long catId);

    Optional<Event> findFirstByOrderByCreatedOnAsc();

    Optional<Event> findFirstByOrderByCreatedOnDesc();

}