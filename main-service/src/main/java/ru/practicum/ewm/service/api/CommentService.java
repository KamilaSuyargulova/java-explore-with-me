package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentRequestDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, CommentRequestDto commentDto);

    CommentDto patchByUser(Long userId, Long commentId, CommentRequestDto commentDto);

    List<CommentDto> getCommentUser(Long userId);

    CommentDto getUserCommentByUserAndCommentId(Long userId, Long commentId);

    List<CommentDto> getCommentEvent(Long eventId, Integer from, Integer size);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> search(String text, Integer from, Integer size);
}