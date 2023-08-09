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
import ru.practicum.dto.events.MapperUserEvents;
import ru.practicum.dto.events.UpdateEventAdminRequest;
import ru.practicum.enums.EventStates;
import ru.practicum.handler.BadRequestDate;
import ru.practicum.handler.ConflictStateException;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.LocationRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminEventsService {

    private final EventsRepository eventsRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatClient statClient;

    public AdminEventsService(@Autowired EventsRepository eventsRepository, CategoryRepository categoryRepository, LocationRepository locationRepository, StatClient statClient) {
        this.eventsRepository = eventsRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.statClient = statClient;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<EventDto> getEvents(List<Long> userIds, List<String> states, List<Long> categories,
                                          Timestamp rangeStart, Timestamp rangeEnd, Long from, Long size) {
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));

        if (userIds == null && states == null && categories == null && rangeStart == null && rangeEnd == null) {
            return MapperUserEvents.toEventFullColl(eventsRepository.findAll(page).toList());
        }

        List<Event> events = new ArrayList<>();

        if (rangeStart != null && rangeEnd != null) {
            events = eventsRepository.getEventsByParameters(userIds, states, categories,
                    rangeStart, rangeEnd, page);
        } else {
            events = eventsRepository.getEventsByParameters(userIds, states, categories, page);
        }
        return MapperUserEvents.toEventFullColl(events);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EventDto patchEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (!eventsRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%s was not found", eventId));
        }

        Event event = eventsRepository.getReferenceById(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(updateEventAdminRequest.getCategory()));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            if (Timestamp.valueOf(updateEventAdminRequest.getEventDate()).before(Timestamp.from(Instant.now()))) {
                throw new BadRequestDate("Date is before");
            }
            event.setEventDate(Timestamp.valueOf(updateEventAdminRequest.getEventDate()));
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationRepository.save(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit().longValue());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (!eventsRepository.getReferenceById(eventId).getState().equals(EventStates.PENDING.toString())) {
                throw new ConflictStateException("Event is not PENDING status");
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStates.PUBLISH_EVENT.toString())) {
                event.setState(EventStates.PUBLISHED.toString());
                event.setPublishedOn(Timestamp.from(Instant.now()));
            }
            if (updateEventAdminRequest.getStateAction().equals(EventStates.REJECT_EVENT.toString())) {
                event.setState(EventStates.CANCELED.toString());
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        List<Long> ids = new ArrayList<>();
        ids.add(eventId);
        Long views = getViews(ids).get(eventId);
        return MapperUserEvents.toEventFull(eventsRepository.save(event), views);
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
