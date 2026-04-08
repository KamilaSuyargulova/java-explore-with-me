package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.service.api.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getPrivateUserEvents(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getPrivateUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getPrivateUserEvent(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        return eventService.getPrivateUserEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getPrivateUserEventRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        return eventService.getPrivateUserEventRequests(userId, eventId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createPrivateEvent(@PathVariable Long userId,
                                           @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createPrivateEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updatePrivateUserEvent(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        return eventService.updatePrivateUserEvent(userId, eventId, updateRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return eventService.updateRequestStatus(userId, eventId, updateRequest);
    }
}