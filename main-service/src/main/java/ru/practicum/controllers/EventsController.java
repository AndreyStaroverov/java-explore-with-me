package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatClient;
import ru.practicum.dto.events.EventDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
public class EventsController {

    private final EventService eventService;
    private final StatClient statClient;
    private static final String SERVICE_NAME = "main-service";

    public EventsController(@Autowired EventService eventService, StatClient statClient) {
        this.eventService = eventService;
        this.statClient = statClient;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                               @RequestParam(name = "categories", required = false) List<Long> categories,
                                               @RequestParam(name = "paid", required = false) Boolean paid,
                                               @RequestParam(name = "rangeStart", required = false) Timestamp rangeStart,
                                               @RequestParam(name = "rangeEnd", required = false) Timestamp rangeEnd,
                                               @RequestParam(name = "onlyAvailable", defaultValue = "false")
                                               Boolean onlyAvailable,
                                               @RequestParam(name = "sort", required = false) String sort,
                                               @RequestParam(name = "from", defaultValue = "0") @Min(0) Long from,
                                               @RequestParam(name = "size", defaultValue = "10") @Min(1) Long size,
                                               HttpServletRequest request) {
        statClient.saveHit(HitDto.builder()
                .app(SERVICE_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(Timestamp.from(Instant.now()).toString())
                .build());
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventsById(@PathVariable @Positive Long eventId, HttpServletRequest request) {
        statClient.saveHit(HitDto.builder()
                .app(SERVICE_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(Timestamp.from(Instant.now()).toString())
                .build());
        return eventService.getEventById(eventId);
    }


}
