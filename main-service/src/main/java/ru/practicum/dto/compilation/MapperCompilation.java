package ru.practicum.dto.compilation;

import ru.practicum.dto.events.MapperUserEvents;
import ru.practicum.model.Compilation;

import java.util.ArrayList;
import java.util.Collection;

public final class MapperCompilation {

    private MapperCompilation() {
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(new ArrayList<>(MapperUserEvents.toEventShortDtoColl(compilation.getEvents())))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDtoWithoutEvents(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(new ArrayList<>())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Collection<CompilationDto> toCompilationDtoColl(Collection<Compilation> compilations) {
        Collection<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationDtos.add(CompilationDto.builder()
                    .id(compilation.getId())
                    .events(new ArrayList<>(MapperUserEvents.toEventShortDtoColl(compilation.getEvents())))
                    .pinned(compilation.getPinned())
                    .title(compilation.getTitle())
                    .build());
        }
        return compilationDtos;
    }
}
