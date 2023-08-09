package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.StatClient;
import ru.practicum.dto.events.EventDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
public class EventsController {

    private final EventService eventService;
    private final StatClient statClient = new StatClient(new RestTemplate());

    public EventsController(@Autowired EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                               @RequestParam(name = "categories", required = false) List<Long> categories,
                                               @RequestParam(name = "paid", required = false) Boolean paid,
                                               @RequestParam(name = "rangeStart", required = false) Timestamp rangeStart,
                                               @RequestParam(name = "rangeEnd", required = false) Timestamp rangeEnd,
                                               @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false")
                                               Boolean onlyAvailable,
                                               @RequestParam(name = "sort", required = false) String sort,
                                               @RequestParam(name = "from", required = false, defaultValue = "0")
                                               @PositiveOrZero Long from,
                                               @RequestParam(name = "size", required = false, defaultValue = "10")
                                               @Positive Long size,
                                               HttpServletRequest request) {
//        String requestURL = request.getRequestURL().substring(0, request.getRequestURL().indexOf(request.getRequestURI()));
//        statClient.saveHit(HitDto.builder()
//                .app("main-service")
//                .uri(requestURL + "/events")
//                .ip(request.getRemoteAddr())
//                .timestamp(Timestamp.from(Instant.now()).toString())
//                .build());
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventsById(@PathVariable @Positive Long eventId,
                                  HttpServletRequest request) {
//        statClient.saveHit(HitDto.builder()
//                .app("main-service")
//                .uri(request.getRequestURI())
//                .ip(request.getRemoteAddr())
//                .timestamp(Timestamp.from(Instant.now()).toString())
//                .build());
        return eventService.getEventById(eventId);
    }


}
