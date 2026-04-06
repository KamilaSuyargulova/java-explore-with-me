package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.State;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.api.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public UserDto createAdminUser(NewUserRequest newUserRequest) {

        checkUserName(newUserRequest.getName());
        checkUserEmail(newUserRequest.getEmail());
        checkDuplicateEmailUser(newUserRequest.getEmail());
        User user = UserMapper.requestUserMapToUser(newUserRequest);
        userRepository.save(user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> getAdminUsers(List<Long> ids, int from, int size) {

        Pageable pageable = PageRequest.of(from / size, size);
        Page<User> usersPage;
        if (ids != null && !ids.isEmpty()) {
            usersPage = userRepository.findAllByIdIn(ids, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        return usersPage
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAdminUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User с таким Id = " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<ParticipationRequestDto> getPrivateUserRequests(Long userId) {

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addPrivateRequest(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event не найден"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new UserConflictException("Инициатор не может подать заявку на своё событие");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new UserConflictException("Заявка уже существует");
        }
        if (event.getState().equals(State.CANCELED) || event.getState().equals(State.PENDING)) {
            throw new UserConflictException("Event еще не опубликован");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new EventConflictException("Превышен лимит участников Event");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        requestRepository.save(request);

        return RequestMapper.mapToParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelPrivateRequest(Long userId, Long requestId) {

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new RequestValidationException("Заявка не найдена"));
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        return RequestMapper.mapToParticipationRequestDto(request);
    }

    private void checkUserName(String userName) {

        if (userName == null || userName.isBlank()) {
            throw new UserValidationException("Некорректное имя пользователя");
        } else if (userName.length() < 2 || userName.length() > 250) {
            throw new UserValidationException("Некорректное имя пользователя");
        }
    }

    private void checkUserEmail(String userEmail) {

        if (userEmail == null || userEmail.isBlank() || userEmail.length() > 254) {
            throw new UserValidationException("Некорректный email пользователя");
        } else if (!userEmail.contains("@") || userEmail.indexOf("@") != userEmail.lastIndexOf("@")) {
            throw new UserValidationException("Email должен содержать один символ '@'");
        }

        String[] parts = userEmail.split("@");
        String localPart = parts[0];
        String domainPart = parts[1];
        String[] domainParts = domainPart.split("\\.");

        for (String subPart : domainParts) {
            if (subPart.length() > 63) {
                throw new UserValidationException("Каждая часть домена (между точками) должна содержать не более 63 символов");
            }
        }
        if (userEmail.length() < 6) {
            throw new UserValidationException("Некорректный формат email");
        } else if (localPart.length() > 64) {
            throw new UserValidationException("Часть email до '@' должна содержать до 64 символов");
        } else if (domainPart.length() < 3) {
            throw new UserValidationException("Часть email после '@' должна содержать не менее 3 символов");
        } else if (!domainPart.contains(".")) {
            throw new UserValidationException("Часть email после '@' должна содержать хотя бы одну точку (например, gmail.com)");
        }
    }

    protected void checkDuplicateEmailUser(String userEmail) {

        userRepository.findByEmail(userEmail)
                .ifPresent(u -> {
                    throw new UserConflictException("Пользователь с таким email уже существует ");
                });
    }
}