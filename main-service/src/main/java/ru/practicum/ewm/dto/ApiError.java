package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String reason;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
}