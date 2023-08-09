package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Hit;
import ru.practicum.service.HitService;

import java.util.Collection;

@RestController
public class StatsController {

    private final HitService hitService;

    public StatsController(@Autowired HitService hitService) {
        this.hitService = hitService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Hit saveHit(@RequestBody HitDto hit) {
        return hitService.saveHit(hit);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ViewStatsDto> getStats(@RequestParam(value = "start", required = true) String start,
                                             @RequestParam(value = "end", required = true) String end,
                                             @RequestParam(value = "uris", required = false) String[] uris,
                                             @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        return hitService.getStats(start, end, uris, unique);
    }


}
