package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.events.*;
import ru.practicum.service.UserEventsService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserEventsController {

    private final UserEventsService userEventsService;


    public UserEventsController(@Autowired UserEventsService userEventsService) {
        this.userEventsService = userEventsService;
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDtoGet> getEvents(@PathVariable @Valid @Positive Long userId,
                                                  @RequestParam(value = "from", defaultValue = "0")
                                                  @Valid @Min(0) Long from,
                                                  @RequestParam(value = "size", defaultValue = "10")
                                                  @Valid @Min(1) Long size) {
        return userEventsService.getEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto postEvent(@PathVariable @Valid @Positive Long userId,
                              @Valid @RequestBody NewEventDto newEventDto) {
        return userEventsService.postEvent(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventById(@PathVariable @Valid @Positive Long userId,
                                 @PathVariable @Valid @Positive Long eventId) {
        return userEventsService.getEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto patchEvent(@PathVariable @Valid @Positive Long userId,
                               @PathVariable @Valid @Positive Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return userEventsService.patchEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getEventByIdRequests(@PathVariable @Valid @Positive Long userId,
                                                                    @PathVariable @Valid @Positive Long eventId) {
        return userEventsService.getEventByIdRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchEventRequests(@PathVariable @Valid @Positive Long userId,
                                                             @PathVariable @Valid @Positive Long eventId,
                                                             @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                     eventRequestStatusUpdateRequest) {
        return userEventsService.patchEventRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }

}
