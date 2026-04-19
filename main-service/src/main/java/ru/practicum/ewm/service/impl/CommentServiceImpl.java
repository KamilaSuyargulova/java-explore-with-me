package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.State;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.api.CommentService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Нельзя комментировать неопубликованное событие");
        }

        Comment comment = CommentMapper.toComment(commentDto, event, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto patchByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является автором комментария");
        }

        if (LocalDateTime.now().isAfter(comment.getCreated().plusHours(1))) {
            throw new ValidationException("Редактировать комментарий можно только в течение часа после создания");
        }

        comment.setText(updateCommentDto.getText());
        comment.setLastUpdatedOn(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentUser(Long userId) {
        getUser(userId);
        return commentRepository.findByAuthorId(userId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getUserCommentByUserAndCommentId(Long userId, Long commentId) {
        getUser(userId);
        Comment comment = commentRepository.findByAuthorIdAndId(userId, commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentEvent(Long eventId, Integer from, Integer size) {
        getEvent(eventId);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является автором комментария");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        getComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> search(String text, Integer from, Integer size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.search(text, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + id + " не найден"));
    }
}