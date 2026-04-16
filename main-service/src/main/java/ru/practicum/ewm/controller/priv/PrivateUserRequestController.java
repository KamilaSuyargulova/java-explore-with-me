package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.exception.RequestValidationException;
import ru.practicum.ewm.service.api.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateUserRequestController {

    private final UserService userService;

    @GetMapping
    public List<ParticipationRequestDto> getPrivateUserRequests(@PathVariable Long userId) {
        return userService.getPrivateUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addPrivateRequest(@PathVariable Long userId, @RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            throw new RequestValidationException("Не указан eventId");
        }
        return userService.addPrivateRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelPrivateRequest(@PathVariable Long userId,
                                                        @PathVariable Long requestId) {
        return userService.cancelPrivateRequest(userId, requestId);
    }
}