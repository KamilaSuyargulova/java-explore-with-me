package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventFullDto mapToEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setInitiator(UserMapper.mapToUserDto(event.getInitiator()));
        eventFullDto.setLocation(event.getLocation());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        return eventFullDto;
    }

    public static EventShortDto eventFullDtoMapToEventShortDto(EventFullDto eventFullDto) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(eventFullDto.getId());
        eventShortDto.setAnnotation(eventFullDto.getAnnotation());
        eventShortDto.setCategory(eventFullDto.getCategory());
        eventShortDto.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        eventShortDto.setEventDate(eventFullDto.getEventDate());
        eventShortDto.setInitiator(eventFullDto.getInitiator());
        eventShortDto.setPaid(eventFullDto.getPaid());
        eventShortDto.setTitle(eventFullDto.getTitle());
        eventShortDto.setViews(eventFullDto.getViews());
        return eventShortDto;
    }

    public static EventShortDto eventMapToEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(UserMapper.mapToUserDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());
        return eventShortDto;
    }


    public static Event mapToEvent(EventFullDto eventFullDto, Category category, User initiator) {
        Event event = new Event();

        event.setId(eventFullDto.getId());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(category);
        event.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        event.setCreatedOn(eventFullDto.getCreatedOn());
        event.setDescription(eventFullDto.getDescription());
        event.setEventDate(eventFullDto.getEventDate());
        event.setInitiator(initiator);
        event.setPaid(eventFullDto.getPaid());
        event.setParticipantLimit(eventFullDto.getParticipantLimit());
        event.setPublishedOn(eventFullDto.getPublishedOn());
        event.setRequestModeration(eventFullDto.getRequestModeration());
        event.setState(eventFullDto.getState());
        event.setTitle(eventFullDto.getTitle());
        event.setViews(eventFullDto.getViews());
        return event;
    }

    public static Event mapNewEventDtoToEvent(NewEventDto dto, Category category, User initiator) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true);
        event.setTitle(dto.getTitle());
        event.setCategory(category);
        event.setInitiator(initiator);

        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        event.setCreatedOn(LocalDateTime.now());

        return event;
    }

    public static void updateEventFromUserRequest(UpdateEventUserRequest request, Event event) {
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getLocation() != null) {
            event.setLocation(LocationMapper.mapToLocation(request.getLocation()));
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
    }
}