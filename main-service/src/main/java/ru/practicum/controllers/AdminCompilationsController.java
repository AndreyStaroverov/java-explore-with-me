package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.AdminCompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/compilations")
@Validated
public class AdminCompilationsController {

    private final AdminCompilationService adminCompilationService;

    public AdminCompilationsController(@Autowired AdminCompilationService adminCompilationService) {
        this.adminCompilationService = adminCompilationService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return adminCompilationService.postCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Valid @Positive Long compId) {
        adminCompilationService.deleteComp(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patchCompilations(@PathVariable @Valid @Positive Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return adminCompilationService.patchCompilation(compId, updateCompilationRequest);
    }
}
