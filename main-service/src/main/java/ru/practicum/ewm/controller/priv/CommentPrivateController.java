package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.service.api.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST запрос на добавление комментария от userId={} к eventId={}", userId, eventId);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/users/{userId}/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("PATCH запрос на обновление комментария id={} от userId={}", commentId, userId);
        return commentService.patchByUser(userId, commentId, updateCommentDto);
    }

    @GetMapping("/users/{userId}")
    public List<CommentDto> getUserComments(@PathVariable Long userId) {
        log.info("GET запрос на получение комментариев пользователя userId={}", userId);
        return commentService.getCommentUser(userId);
    }

    @GetMapping("/users/{userId}/{commentId}")
    public CommentDto getUserComment(@PathVariable Long userId,
                                     @PathVariable Long commentId) {
        log.info("GET запрос на получение комментария id={} от userId={}", commentId, userId);
        return commentService.getUserCommentByUserAndCommentId(userId, commentId);
    }

    @DeleteMapping("/users/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("DELETE запрос на удаление комментария id={} пользователем userId={}", commentId, userId);
        commentService.deleteComment(userId, commentId);
    }
}