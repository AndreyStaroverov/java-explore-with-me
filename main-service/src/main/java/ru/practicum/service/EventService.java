package ru.practicum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.events.EventDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.MapperUserEvents;
import ru.practicum.enums.EventStates;
import ru.practicum.handler.BadRequestDate;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.repository.EventsRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventsRepository eventsRepository;
    private final StatClient statClient;

    public EventService(@Autowired EventsRepository eventsRepository, StatClient statClient) {
        this.eventsRepository = eventsRepository;
        this.statClient = statClient;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                               Timestamp rangeStart, Timestamp rangeEnd, Boolean onlyAvailable,
                                               String sort, Long from, Long size) {
        if (categories != null && categories.contains(0L)) {
            throw new BadRequestDate("Categories is Bad");
        }
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = Timestamp.from(Instant.now());
        }
        List<Event> events = new ArrayList<>();
        events = eventsRepository.getEventsByParametersForPublic(text, categories, paid,
                EventStates.PUBLISHED.toString(), rangeStart, rangeEnd, page);
        List<EventShortDto> eventShortDtos = new ArrayList<>();

        if (onlyAvailable != null) {
            if (onlyAvailable) {
                for (Event event : events) {
                    if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                        events.remove(event);
                    }
                }
            }
        }
        for (Event event : events) {
            List<Long> ids = new ArrayList<>();
            ids.add(event.getId());
            Long views = getViews(ids).get(event.getId());
            eventShortDtos.add(MapperUserEvents.toEventShortDto(event, views));
        }
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;
                case "VIEWS":
                    eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews));
                    break;
                default:
                    throw new BadRequestDate("BadSort");
            }
        }
        return eventShortDtos;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public EventDto getEventById(Long eventId) {
        if (!eventsRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%s not exist", eventId));
        }
        if (!eventsRepository.getReferenceById(eventId).getState().equals(EventStates.PUBLISHED.toString())) {
            throw new NotFoundException("Event is not published");
        }
        if (eventsRepository.getReferenceById(eventId).getPublishedOn() != null) {
            List<Long> ids = new ArrayList<>();
            ids.add(eventId);
            Long views = getViews(ids).get(eventId);
            return MapperUserEvents.toEventFull(eventsRepository.getReferenceById(eventId), views);
        } else {
            throw new BadRequestDate("Event is not Published");
        }
    }

    public Map<Long, Long> getViews(List<Long> ids) {
        List<String> uris = ids.stream().map(id -> "/events/" + id).collect(Collectors.toList());
        String[] urisArray = uris.toArray(uris.toArray(new String[uris.size()]));

        ResponseEntity<Object> response = statClient.getStats(Timestamp.from(Instant.ofEpochMilli(334638053)).toString(),
                Timestamp.from(Instant.now()).toString(), urisArray, true);

        Object responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        Collection<ViewStatsDto> viewStatsDtos =
                objectMapper.convertValue(responseBody, new TypeReference<Collection<ViewStatsDto>>() {
                });

        assert viewStatsDtos != null;

        Map<Long, Long> viewsMap = new HashMap<>();

        for (ViewStatsDto viewStatsDto : viewStatsDtos) {
            String str = viewStatsDto.getUri();
            int lastIndex = str.lastIndexOf("/") + 1;
            Long id = Long.parseLong(str.substring(lastIndex));
            viewsMap.put(id, viewStatsDto.getHits());
        }

        return viewsMap;
    }
}
