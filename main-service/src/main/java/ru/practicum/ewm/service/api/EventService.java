package ru.practicum.ewm.service.api;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAdminEvents(EventAdminSearchParams params);

    List<EventShortDto> getPublicEvents(EventPublicSearchParams params, HttpServletRequest request);

    EventFullDto updateAdminEvent(Long eventId, AdminUpdateEventRequest request);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getPrivateUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createPrivateEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getPrivateUserEvent(Long userId, Long eventId);

    EventFullDto updatePrivateUserEvent(Long userId, Long eventId, UserUpdateEventRequest updateRequest);

    List<ParticipationRequestDto> getPrivateUserEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);
}