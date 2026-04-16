package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setStatus(String.valueOf(request.getStatus()));
        dto.setCreated(request.getCreated().truncatedTo(ChronoUnit.SECONDS));
        return dto;
    }

    public static ParticipationRequest mapToRequest(User requester, Event event) {
        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(requester);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}