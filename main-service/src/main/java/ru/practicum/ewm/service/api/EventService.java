package ru.practicum.ewm.service.api;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                        String sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getPrivateUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createPrivateEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getPrivateUserEvent(Long userId, Long eventId);

    EventFullDto updatePrivateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    List<ParticipationRequestDto> getPrivateUserEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);
}