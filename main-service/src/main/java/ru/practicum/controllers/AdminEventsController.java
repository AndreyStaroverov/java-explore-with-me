package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.events.EventDto;
import ru.practicum.dto.events.UpdateEventAdminRequest;
import ru.practicum.service.AdminEventsService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
public class AdminEventsController {

    private final AdminEventsService adminEventsService;

    public AdminEventsController(@Autowired AdminEventsService adminEventsService) {
        this.adminEventsService = adminEventsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventDto> getEvents(@RequestParam(name = "users", required = false) List<Long> userIds,
                                          @RequestParam(name = "states", required = false) List<String> states,
                                          @RequestParam(name = "categories", required = false) List<Long> categories,
                                          @RequestParam(name = "rangeStart", required = false) Timestamp rangeStart,
                                          @RequestParam(name = "rangeEnd", required = false) Timestamp rangeEnd,
                                          @RequestParam(name = "from", required = false, defaultValue = "0")
                                          @Min(0) Long from,
                                          @RequestParam(name = "size", required = false, defaultValue = "10")
                                          @Min(1) Long size) {
        return adminEventsService.getEvents(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto patchEvents(@PathVariable @Positive Long eventId,
                                @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return adminEventsService.patchEvent(eventId, updateEventAdminRequest);
    }
}
