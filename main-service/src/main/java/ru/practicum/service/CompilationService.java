package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.MapperCompilation;
import ru.practicum.handler.NotFoundException;
import ru.practicum.repository.CompilationsRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CompilationService {

    private final CompilationsRepository compilationsRepository;

    public CompilationService(@Autowired CompilationsRepository compilationsRepository) {
        this.compilationsRepository = compilationsRepository;
    }

    public Collection<CompilationDto> getCompilations(Boolean pinned, Long from, Long size) {
        if (from != null && size != null && pinned != null) {
            Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
            return MapperCompilation.toCompilationDtoColl(compilationsRepository.findAllByPinned(pinned, page));
        }
        if (from != null && size != null && pinned == null) {
            Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
            return MapperCompilation.toCompilationDtoColl(compilationsRepository.findAll(page).toList());
        }
        if (from == null && size == null && pinned != null) {
            return MapperCompilation.toCompilationDtoColl(compilationsRepository.findAllByPinned(pinned));
        }
        return MapperCompilation.toCompilationDtoColl(compilationsRepository.findAll());
    }

    public CompilationDto getCompiltionById(Long compId) {
        if (!compilationsRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id=%s was not found", compId));
        }
        return MapperCompilation.toCompilationDto(compilationsRepository.getReferenceById(compId));
    }
}
