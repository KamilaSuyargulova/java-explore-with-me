package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.ewm.dto.ApiError;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ApiError> handleUserValidationException(UserValidationException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.NOT_FOUND,
                "User Not Found",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserConflictException.class)
    public ResponseEntity<ApiError> handleUserConflictException(UserConflictException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.CONFLICT,
                "User Conflict",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CategoryValidationException.class)
    public ResponseEntity<ApiError> handleCategoryValidationException(CategoryValidationException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.NOT_FOUND,
                "Category Not Found",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CategoryConflictException.class)
    public ResponseEntity<ApiError> handleCategoryConflictException(CategoryConflictException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.CONFLICT,
                "Category Conflict",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EventValidationException.class)
    public ResponseEntity<ApiError> handleEventValidationException(EventValidationException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiError> handleEventNotFoundException(EventNotFoundException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.NOT_FOUND,
                "Event Not Found",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EventConflictException.class)
    public ResponseEntity<ApiError> handleEventConflictException(EventConflictException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.CONFLICT,
                "Event Conflict",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ApiError> handleRequestValidationException(RequestValidationException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RequestConflictException.class)
    public ResponseEntity<ApiError> handleRequestConflictException(RequestConflictException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.CONFLICT,
                "Request Conflict",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                "Некорректный формат данных: " + ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}