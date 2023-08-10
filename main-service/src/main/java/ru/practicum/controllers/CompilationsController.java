package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/compilations")
@Validated
public class CompilationsController {

    private final CompilationService compilationService;

    public CompilationsController(@Autowired CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CompilationDto> getCompilation(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                     @RequestParam(name = "from", defaultValue = "0") @Min(0) Long from,
                                                     @RequestParam(name = "size", defaultValue = "10") @Min(1) Long size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable @Positive Long compId) {
        return compilationService.getCompiltionById(compId);
    }
}
