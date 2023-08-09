package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserRequests.MapperUserRequests;
import ru.practicum.dto.events.*;
import ru.practicum.enums.EventStates;
import ru.practicum.enums.RequestStates;
import ru.practicum.handler.BadRequestDate;
import ru.practicum.handler.ConflictStateException;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.UserRequest;
import ru.practicum.repository.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserEventsService {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRequestRepository userRequestRepository;

    public UserEventsService(@Autowired EventsRepository eventsRepository, UserRepository userRepository, CategoryRepository categoryRepository, LocationRepository locationRepository, UserRequestRepository userRequestRepository) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.userRequestRepository = userRequestRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<EventShortDtoGet> getEvents(Long userId, Long from, Long size) {
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
        return MapperUserEvents.toEventShortDtoGetColl(eventsRepository.findAllByInitiator_id(userId, page).toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EventDto postEvent(NewEventDto newEventDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%s not exist", userId));
        }
        if (!Timestamp.valueOf(newEventDto.getEventDate()).after(Timestamp.from(Instant.now()))) {
            throw new BadRequestDate("Date is before now");
        }
        if (newEventDto.getLocation() != null) {
            newEventDto.setLocation(locationRepository.save(newEventDto.getLocation()));
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        return MapperUserEvents.toEventFullPost(eventsRepository.save(MapperUserEvents.toEventFromNewEvDto(newEventDto,
                categoryRepository.getReferenceById(newEventDto.getCategory()), userRepository.getReferenceById(userId))));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public EventDto getEventById(Long userId, Long eventId) {
        checkEventAndUser(eventId, userId);
        if (!eventsRepository.getReferenceById(eventId).getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Not your event");
        }
        return MapperUserEvents.toEventFull(eventsRepository.getReferenceById(eventId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EventDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        checkEventAndUser(eventId, userId);
        if (eventsRepository.getReferenceById(eventId).getState().equals(EventStates.PUBLISHED.toString())) {
            throw new ConflictStateException("Status is PUBLISHED you can't update");
        }
        if (!eventsRepository.getReferenceById(eventId).getInitiator().getId().equals(userId)) {
            throw new NotFoundException(String.format("This event id=%s cant be patch by id=%s", eventId, userId));
        }

        Event event = eventsRepository.getReferenceById(eventId);

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            if (Timestamp.valueOf(updateEventUserRequest.getEventDate()).before(Timestamp.from(Instant.now()
                    .plusSeconds(7200)))) {
                throw new BadRequestDate("Date is invalid");
            }
            event.setEventDate(Timestamp.valueOf(updateEventUserRequest.getEventDate()));
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(locationRepository.save(updateEventUserRequest.getLocation()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit().longValue());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(RequestStates.SEND_TO_REVIEW.toString())) {
                event.setState(EventStates.PENDING.toString());
            }
            if (updateEventUserRequest.getStateAction().equals(RequestStates.CANCEL_REVIEW.toString())) {
                event.setState(EventStates.CANCELED.toString());
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return MapperUserEvents.toEventFullPost(eventsRepository.save(event));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<ParticipationRequestDto> getEventByIdRequests(Long userId, Long eventId) {
        checkEventAndUser(eventId, userId);
        return MapperUserRequests.toPartReqDtoColl(userRequestRepository.findByInitiator_idAndEvent_id(userId, eventId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public EventRequestStatusUpdateResult patchEventRequests(Long userId, Long eventId,
                                                             EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        checkEventAndUser(eventId, userId);
        if (eventsRepository.getReferenceById(eventId).getRequestModeration().equals(false) ||
                eventsRepository.getReferenceById(eventId).getParticipantLimit().equals(0L)) {
            throw new BadRequestDate("Подтверждение не требуется");
        }
        if (eventsRepository.getReferenceById(eventId).getConfirmedRequests()
                .equals(eventsRepository.getReferenceById(eventId).getParticipantLimit())) {
            throw new ConflictStateException("The participant limit has been reached");
        }

        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmedReq = new ArrayList<>();
        List<ParticipationRequestDto> rejectedReq = new ArrayList<>();
        updateResult.setConfirmedRequests(confirmedReq);
        updateResult.setRejectedRequests(rejectedReq);

        Long partLimit = eventsRepository.getReferenceById(eventId).getParticipantLimit();
        Long confReq = eventsRepository.getReferenceById(eventId).getConfirmedRequests();
        List<UserRequest> userRequests = userRequestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());

        for (UserRequest userRequest : userRequests) {
            if (!userRequest.getStatus().equals(EventStates.PENDING.toString())) {
                throw new ConflictStateException("Only request with PENDING status can be changed");
            }
            if (confReq.equals(partLimit)) {
                userRequest.setStatus(RequestStates.REJECTED.toString());
                updateResult.getRejectedRequests().add(MapperUserRequests.toPartReqDto(userRequest));
            }
            if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStates.REJECTED.toString())) {
                userRequest.setStatus(RequestStates.REJECTED.toString());
                updateResult.getRejectedRequests().add(MapperUserRequests.toPartReqDto(userRequest));
            }
            if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStates.CONFIRMED.toString())) {
                userRequest.setStatus(RequestStates.CONFIRMED.toString());
                updateResult.getConfirmedRequests().add(MapperUserRequests.toPartReqDto(userRequest));
                confReq = confReq + 1;
            }
        }
        Event event = eventsRepository.getReferenceById(eventId);
        event.setConfirmedRequests(confReq);
        eventsRepository.save(event);
        userRequestRepository.saveAll(userRequests);
        return updateResult;
    }

    public void checkEventAndUser(Long eventId, Long userId) {
        if (!eventsRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%s not exist", eventId));
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%s not exist", userId));
        }
    }
}
