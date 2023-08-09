package ru.practicum.dto.events;

import ru.practicum.dto.Users.UserShortDto;
import ru.practicum.enums.EventStates;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapperUserEvents {

    public static EventShortDto toEventShortDto(Event event) {
        String date = event.getEvent_date().toLocalDateTime().toString().replace("T", " ");
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                event.getConf_req(),
                event.getDescription(),
                date,
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getPaid(),
                event.getTitle(),
                0L
        );
    }

    public static Collection<EventShortDto> toEventShortDtoColl(Collection<Event> events) {
        Collection<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            String date = event.getEvent_date().toLocalDateTime().toString().replace("T", " ");
            eventShortDtos.add(new EventShortDto(
                    event.getId(),
                    event.getAnnotation(),
                    event.getCategory(),
                    event.getConf_req(),
                    event.getDescription(),
                    date,
                    new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                    event.getPaid(),
                    event.getTitle(),
                    0L
            ));
        }
        return eventShortDtos;
    }

    public static Collection<EventShortDtoGet> toEventShortDtoGetColl(Collection<Event> events) {
        Collection<EventShortDtoGet> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtos.add(EventShortDtoGet.builder()
                    .eventDate(event.getEvent_date().toLocalDateTime())
                    .annotation(event.getAnnotation())
                    .category(event.getCategory())
                    .id(event.getId())
                    .paid(event.getPaid())
                    .title(event.getTitle())
                    .build());
        }
        return eventShortDtos;
    }

    public static Event toEventFromNewEvDto(NewEventDto newEventDto, Category category, User initiator) {
        return Event.builder()
                .id(newEventDto.getId())
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .event_date(Timestamp.valueOf(newEventDto.getEventDate()))
                .conf_req(0L)
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .createdOn(Timestamp.from(Instant.now()))
                .paid(newEventDto.getPaid())
                .part_limit(newEventDto.getParticipantLimit().longValue())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventStates.PENDING.toString())
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventDto toEventFullPost(Event event) {
        String date = event.getEvent_date().toLocalDateTime().toString().replace("T", " ");
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConf_req())
                .createdOn(event.getCreatedOn().toString())
                .description(event.getDescription())
                .eventDate(date)
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getPart_limit().intValue())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(0L)
                .build();
    }

    public static EventDto toEventFull(Event event) {
        String date = event.getEvent_date().toLocalDateTime().toString().replace("T", " ");
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConf_req())
                .createdOn(event.getCreatedOn().toString())
                .description(event.getDescription())
                .eventDate(date)
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getPart_limit().intValue())
                .publishedOn("" + event.getPublishedOn() + "")
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(0L)
                .build();
    }

    public static Collection<EventDto> toEventFullColl(List<Event> events) {
        List<EventDto> eventDtos = new ArrayList<>();
        for (Event event : events) {
            String date = event.getEvent_date().toLocalDateTime().toString().replace("T", " ");
            eventDtos.add(EventDto.builder()
                    .id(event.getId())
                    .annotation(event.getAnnotation())
                    .category(event.getCategory())
                    .confirmedRequests(event.getConf_req())
                    .createdOn(event.getCreatedOn().toString())
                    .description(event.getDescription())
                    .eventDate(date)
                    .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                    .location(event.getLocation())
                    .paid(event.getPaid())
                    .participantLimit(event.getPart_limit().intValue())
                    .publishedOn("" + event.getPublishedOn() + "")
                    .requestModeration(event.getRequestModeration())
                    .state(event.getState())
                    .title(event.getTitle())
                    .views(0L)
                    .build());
        }
        return eventDtos;
    }
}
