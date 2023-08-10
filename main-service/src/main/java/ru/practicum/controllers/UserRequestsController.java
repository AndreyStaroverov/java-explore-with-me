package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserRequests.UserRequestDto;
import ru.practicum.dto.events.ParticipationRequestDto;
import ru.practicum.service.UserRequestsService;

import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserRequestsController {

    private final UserRequestsService userRequestsService;

    public UserRequestsController(@Autowired UserRequestsService userRequestsService) {
        this.userRequestsService = userRequestsService;
    }


    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserRequestDto> getUserRequestById(@PathVariable @Positive Long userId) {
        return userRequestsService.getUserRequestById(userId);
    }

    @PostMapping("{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequest(@PathVariable @Positive Long userId,
                                               @RequestParam(name = "eventId", required = true) @Positive Long eventId) {
        return userRequestsService.postRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto patchRequest(@PathVariable @Positive Long userId,
                                                @PathVariable @Positive Long requestId) {
        return userRequestsService.patchRequest(userId, requestId);
    }
}
