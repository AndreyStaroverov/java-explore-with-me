package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class EventService {

    private final EventsRepository eventsRepository;

    public EventService(@Autowired EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                               Timestamp rangeStart, Timestamp rangeEnd, Boolean onlyAvailable,
                                               String sort, Long from, Long size) {
        if (categories.contains(0L)) {
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
                    if (event.getConf_req() == event.getPart_limit()) {
                        events.remove(event);
                    }
                }
            }
        }
        for (Event event : events) {
            eventShortDtos.add(MapperUserEvents.toEventShortDto(event));
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
            return MapperUserEvents.toEventFull(eventsRepository.getReferenceById(eventId));
        } else {
            throw new BadRequestDate("Event is not Published");
        }
    }
}
