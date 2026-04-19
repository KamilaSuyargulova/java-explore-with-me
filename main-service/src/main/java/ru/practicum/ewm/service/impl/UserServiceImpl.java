package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        User user = UserMapper.requestUserMapToUser(newUserRequest);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
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
            throw new NotFoundException("User с таким Id = " + userId + " не найден");
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
        if (eventId == null) {
            throw new ValidationException("Не указан eventId");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event не найден"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может подать заявку на своё событие");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Event еще не опубликован");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Заявка уже существует");
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Превышен лимит участников Event");
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
                .orElseThrow(() -> new ValidationException("Заявка не найдена"));
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        return RequestMapper.mapToParticipationRequestDto(request);
    }
}