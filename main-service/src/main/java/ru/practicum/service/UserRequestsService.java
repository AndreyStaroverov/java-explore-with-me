package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserRequests.MapperUserRequests;
import ru.practicum.dto.UserRequests.UserRequestDto;
import ru.practicum.dto.events.ParticipationRequestDto;
import ru.practicum.enums.EventStates;
import ru.practicum.enums.RequestStates;
import ru.practicum.handler.ConflictStateException;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.UserRequest;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.repository.UserRequestRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Service
public class UserRequestsService {

    private final UserRequestRepository userRequestRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    public UserRequestsService(@Autowired UserRequestRepository userRequestRepository, UserRepository userRepository,
                               EventsRepository eventsRepository) {
        this.userRequestRepository = userRequestRepository;
        this.userRepository = userRepository;
        this.eventsRepository = eventsRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<UserRequestDto> getUserRequestById(Long userId) {
        checkUser(userId);
        return MapperUserRequests.collectionUserRequestToDto(userRequestRepository.findByRequester_id(userId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationRequestDto postRequest(Long userId, Long eventId) {
        if (!eventsRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%s was not found", eventId));
        }
        checkUser(userId);
        if (!eventsRepository.getReferenceById(eventId).getState().equals(EventStates.PUBLISHED.toString())) {
            throw new ConflictStateException("Only published events can be request from user");
        }

        if (eventsRepository.getReferenceById(eventId).getInitiator().getId() == userId) {
            throw new ConflictStateException("Cant change your event");
        }
        if (eventsRepository.getReferenceById(eventId).getConf_req() == eventsRepository.getReferenceById(eventId).getPart_limit()
                && eventsRepository.getReferenceById(eventId).getPart_limit() != 0L) {
            throw new ConflictStateException("Part_Limit conflict");
        }

        UserRequest userRequest = new UserRequest(0L, Timestamp.from(Instant.now()),
                eventsRepository.getReferenceById(eventId), userRepository.getReferenceById(userId), EventStates.PENDING.toString());

        if (!eventsRepository.getReferenceById(eventId).getRequestModeration()) {
            userRequest.setStatus(RequestStates.CONFIRMED.toString());
            Event event = eventsRepository.getReferenceById(eventId);
            Long confReq = event.getConf_req();
            event.setConf_req(++confReq);
            eventsRepository.save(event);
            return MapperUserRequests.toPartReqDto(userRequestRepository.save(userRequest));
        }
        if (eventsRepository.getReferenceById(eventId).getPart_limit() == 0) {
            userRequest.setStatus(RequestStates.CONFIRMED.toString());
            Event event = eventsRepository.getReferenceById(eventId);
            Long confReq = event.getConf_req();
            event.setConf_req(++confReq);
            eventsRepository.save(event);
            return MapperUserRequests.toPartReqDto(userRequestRepository.save(userRequest));
        }
        return MapperUserRequests.toPartReqDto(userRequestRepository.save(userRequest));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationRequestDto patchRequest(Long userId, Long requestId) {
        checkUser(userId);
        if (!userRequestRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("Request with id=%s was not found", requestId));
        }
        if (Objects.equals(userRequestRepository.getReferenceById(requestId).getRequester().getId(), userId)) {
            UserRequest userRequest = userRequestRepository.getReferenceById(requestId);
            userRequest.setStatus(EventStates.CANCELED.toString());
            return MapperUserRequests.toPartReqDto(userRequestRepository.save(userRequest));
        } else {
            throw new NotFoundException(String.format("Request with id=%s was not found", requestId));
        }
    }

    public void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(userId.toString());
        }
    }
}
