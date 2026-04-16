package ru.practicum.ewm.service.api;

import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createAdminUser(NewUserRequest newUserRequest);

    List<UserDto> getAdminUsers(List<Long> ids, int from, int size);

    void deleteAdminUser(Long userId);

    List<ParticipationRequestDto> getPrivateUserRequests(Long userId);

    ParticipationRequestDto addPrivateRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelPrivateRequest(Long userId, Long requestId);
}