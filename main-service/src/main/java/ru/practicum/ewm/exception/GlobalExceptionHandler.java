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

    @ExceptionHandler({
            RequestConflictException.class,
            UserConflictException.class,
            EventConflictException.class,
            CategoryConflictException.class
    })
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler({
            RequestValidationException.class,
            EventValidationException.class,
            UserValidationException.class,
            CategoryValidationException.class
    })
    public ResponseEntity<ApiError> handleValidation(RuntimeException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.BAD_REQUEST,
                "Incorrectly made request.",
                "Неверный формат параметра: " + ex.getName(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        ApiError error = new ApiError(
                ApiError.ErrorStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage(),
                List.of(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}