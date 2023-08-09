package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.events.EventDto;
import ru.practicum.dto.events.MapperUserEvents;
import ru.practicum.dto.events.UpdateEventAdminRequest;
import ru.practicum.enums.EventStates;
import ru.practicum.handler.BadRequestDate;
import ru.practicum.handler.ConflictDateException;
import ru.practicum.handler.ConflictStateException;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.LocationRepository;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AdminEventsService {

    private final EventsRepository eventsRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public AdminEventsService(@Autowired EventsRepository eventsRepository, CategoryRepository categoryRepository, LocationRepository locationRepository) {
        this.eventsRepository = eventsRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
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
        return MapperUserEvents.toEventFull(eventsRepository.save(event));
    }

    public void checkTime(Long eventId) {
        LocalDateTime eventDate = eventsRepository.getReferenceById(eventId).getEventDate().toLocalDateTime();
        LocalDateTime published = Timestamp.from(Instant.now()).toLocalDateTime();
        Duration duration = Duration.between(eventDate, published);
        if (duration.toMinutes() <= 60) {
            throw new ConflictDateException("EventDate is so far");
        }
    }
}
