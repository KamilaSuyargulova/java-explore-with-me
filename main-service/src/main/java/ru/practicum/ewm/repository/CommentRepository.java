package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.comment.CountCommentsByEventDto;
import ru.practicum.ewm.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    List<Comment> findByAuthorId(Long userId);

    Optional<Comment> findByAuthorIdAndId(Long userId, Long id);

    @Query("SELECT new ru.practicum.ewm.dto.comment.CountCommentsByEventDto(c.event.id, COUNT(c)) " +
            "FROM Comment c WHERE c.event.id IN ?1 GROUP BY c.event.id")
    List<CountCommentsByEventDto> countCommentByEvent(List<Long> eventIds);

    @Query("SELECT c FROM Comment c WHERE LOWER(c.text) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Comment> search(String text, Pageable pageable);
}