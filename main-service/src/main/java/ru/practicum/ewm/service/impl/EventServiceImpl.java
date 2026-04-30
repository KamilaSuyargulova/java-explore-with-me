package ru.practicum.ewm.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatisticClient;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.dto.State;
import ru.practicum.ewm.dto.comment.CountCommentsByEventDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.api.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatisticClient statisticClient;
    private final CommentRepository commentRepository;

    @Override
    public List<EventFullDto> getAdminEvents(EventAdminSearchParams params) {
        Pageable pageRequest = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        List<State> stateEnums = null;
        if (params.getStates() != null && !params.getStates().isEmpty()) {
            stateEnums = params.getStates().stream()
                    .map(State::valueOf)
                    .collect(Collectors.toList());
        }

        Page<Event> events = eventRepository.findEventsByAdminFilters(
                params.getUsers(),
                stateEnums,
                params.getCategories(),
                params.getRangeStart(),
                params.getRangeEnd(),
                pageRequest
        );

        List<EventFullDto> result = events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());

        setViewsToEventFullDtos(result);
        setCommentsCountToEventFullDtos(result);
        return result;
    }

    @Override
    public EventFullDto updateAdminEvent(Long eventId, AdminUpdateEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event с таким номером = " + eventId + " не найден"));

        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateActionAdmin.REJECT_EVENT)
                    && event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Event" + eventId + " уже опубликован и не может быть отменен");
            } else if (request.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)
                    && event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Event " + eventId + " уже опубликован");
            } else if (request.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)
                    && event.getState().equals(State.PENDING)) {
                if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException("Дата события должна быть не ранее чем за час от текущего момента");
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (request.getStateAction().equals(StateActionAdmin.REJECT_EVENT)
                    && event.getState().equals(State.PENDING)) {
                event.setState(State.CANCELED);
            } else if (request.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)
                    && event.getState().equals(State.CANCELED)) {
                throw new ConflictException("Event " + eventId + " был отменен и не может быть опубликован");
            }
        }
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            LocalDateTime newDate = request.getEventDate();
            if (newDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Дата Event должна быть не менее чем на 1 час позже от текущего времени.");
            }
            event.setEventDate(newDate);
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category с таким номером = " + request.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (request.getLocation() != null) {
            event.setLocation(LocationMapper.mapToLocation(request.getLocation()));
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        Event updated = eventRepository.save(event);
        EventFullDto result = EventMapper.mapToEventFullDto(updated);
        setViewsToEventFullDto(result);
        setCommentsCountToEventFullDto(result);
        return result;
    }

    @Override
    public List<EventShortDto> getPublicEvents(EventPublicSearchParams params, HttpServletRequest request) {
        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        if (rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Время окончания события не должно быть раньше времени начала");
        }

        Sort sortObj = Sort.by(Sort.Order.asc("eventDate"));
        Pageable pageRequest = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(), sortObj);
        Page<Event> events;

        if (params.getText() == null) {
            events = eventRepository.searchPublicEvents(
                    params.getCategories(),
                    params.getPaid(),
                    params.getOnlyAvailable(),
                    pageRequest
            );
        } else {
            events = eventRepository.searchPublicEventsAllParam(
                    params.getText(),
                    params.getCategories(),
                    params.getPaid(),
                    rangeStart,
                    rangeEnd,
                    params.getOnlyAvailable(),
                    pageRequest
            );
        }

        statisticClient.endpointHit(request);

        List<EventShortDto> result = events.stream()
                .map(EventMapper::eventMapToEventShortDto)
                .toList();

        setViewsToEventShortDtos(result);
        setCommentsCountToEventShortDtos(result);

        if ("VIEWS".equalsIgnoreCase(params.getSort())) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event не найден: " + eventId));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Event не опубликован: " + eventId);
        }

        statisticClient.endpointHit(request);

        EventFullDto result = EventMapper.mapToEventFullDto(event);
        setViewsToEventFullDto(result);
        setCommentsCountToEventFullDto(result);
        return result;
    }

    @Override
    public List<EventShortDto> getPrivateUserEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        List<EventShortDto> result = eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::eventMapToEventShortDto)
                .toList();
        setViewsToEventShortDtos(result);
        setCommentsCountToEventShortDtos(result);
        return result;
    }

    @Override
    public EventFullDto createPrivateEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User не найден"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category не найдена"));

        Event event = EventMapper.mapNewEventDtoToEvent(newEventDto, category, initiator);
        event.setConfirmedRequests(0L);
        event.setPublishedOn(null);
        event.setState(State.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        if (newEventDto.getEventDate() != null) {
            if (newEventDto.getEventDate().isBefore(event.getCreatedOn().plusHours(2))) {
                throw new ValidationException("Дата Event должна быть не ранее чем через 2 часа от текущего момента");
            }
        }

        Event saved = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(saved);
    }

    @Override
    public EventFullDto getPrivateUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event не найден или не принадлежит User"));
        EventFullDto result = EventMapper.mapToEventFullDto(event);
        setViewsToEventFullDto(result);
        setCommentsCountToEventFullDto(result);
        return result;
    }

    @Override
    public EventFullDto updatePrivateUserEvent(Long userId, Long eventId, UserUpdateEventRequest updateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ConflictException("Event не найден или не принадлежит User"));

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя редактировать опубликованное событие");
        }

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals(StateActionUser.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            } else if (updateRequest.getStateAction().equals(StateActionUser.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата Event должна быть не ранее чем через 2 часа от текущего момента");
            }
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category не найдена"));
            event.setCategory(category);
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(LocationMapper.mapToLocation(updateRequest.getLocation()));
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        Event updated = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(updated);
    }

    @Override
    public List<ParticipationRequestDto> getPrivateUserEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event с id=" + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("User не является инициатором события");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event с id=" + eventId + " не найдено"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("User не является инициатором события");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Превышен лимит участников Event");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllById(updateRequest.getRequestIds());
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Можно изменять только заявки в статусе PENDING");
            }

            boolean eventLimit = event.getParticipantLimit() != 0 &&
                    event.getConfirmedRequests() >= event.getParticipantLimit();

            if (updateRequest.getStatus() == RequestStatus.CONFIRMED && !eventLimit) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        participationRequestRepository.saveAll(requests);
        eventRepository.save(event);

        return new EventRequestStatusUpdateResult(
                confirmedRequests.stream().map(RequestMapper::mapToParticipationRequestDto).toList(),
                rejectedRequests.stream().map(RequestMapper::mapToParticipationRequestDto).toList()
        );
    }

    private void setViewsToEventFullDtos(List<EventFullDto> events) {
        if (events.isEmpty()) return;

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());

        LocalDateTime start = LocalDateTime.now().minusYears(100);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStats> stats = statisticClient.getStats(start, end, uris, true);

        Map<Long, Long> viewMap = stats.stream()
                .collect(Collectors.toMap(
                        s -> Long.parseLong(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                        ViewStats::getHits,
                        (v1, v2) -> v1
                ));

        events.forEach(e -> e.setViews(viewMap.getOrDefault(e.getId(), 0L)));
    }

    private void setViewsToEventFullDto(EventFullDto event) {
        List<String> uris = List.of("/events/" + event.getId());
        LocalDateTime start = LocalDateTime.now().minusYears(100);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStats> stats = statisticClient.getStats(start, end, uris, true);

        long views = 0;
        if (stats != null && !stats.isEmpty()) {
            views = stats.get(0).getHits();
        }
        event.setViews(views);
    }

    private void setViewsToEventShortDtos(List<EventShortDto> events) {
        if (events.isEmpty()) return;

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());

        LocalDateTime start = LocalDateTime.now().minusYears(100);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStats> stats = statisticClient.getStats(start, end, uris, true);

        Map<Long, Long> viewMap = stats.stream()
                .collect(Collectors.toMap(
                        s -> Long.parseLong(s.getUri().substring(s.getUri().lastIndexOf('/') + 1)),
                        ViewStats::getHits,
                        (v1, v2) -> v1
                ));

        events.forEach(e -> e.setViews(viewMap.getOrDefault(e.getId(), 0L)));
    }

    private void setCommentsCountToEventFullDtos(List<EventFullDto> events) {
        if (events.isEmpty()) return;

        List<Long> eventIds = events.stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList());

        List<CountCommentsByEventDto> counts = commentRepository.countCommentByEvent(eventIds);
        Map<Long, Long> countMap = counts.stream()
                .collect(Collectors.toMap(
                        CountCommentsByEventDto::getEventId,
                        CountCommentsByEventDto::getCountComments
                ));

        events.forEach(e -> e.setCommentsCount(countMap.getOrDefault(e.getId(), 0L)));
    }

    private void setCommentsCountToEventFullDto(EventFullDto event) {
        List<CountCommentsByEventDto> counts = commentRepository.countCommentByEvent(List.of(event.getId()));
        Long count = counts.isEmpty() ? 0L : counts.get(0).getCountComments();
        event.setCommentsCount(count);
    }

    private void setCommentsCountToEventShortDtos(List<EventShortDto> events) {
        if (events.isEmpty()) return;

        List<Long> eventIds = events.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());

        List<CountCommentsByEventDto> counts = commentRepository.countCommentByEvent(eventIds);
        Map<Long, Long> countMap = counts.stream()
                .collect(Collectors.toMap(
                        CountCommentsByEventDto::getEventId,
                        CountCommentsByEventDto::getCountComments
                ));

        events.forEach(e -> e.setCommentsCount(countMap.getOrDefault(e.getId(), 0L)));
    }
}