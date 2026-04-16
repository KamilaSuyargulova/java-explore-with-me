package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.ewm.dto.LocationDto;

import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;

    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    private Long participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    private String title;
}