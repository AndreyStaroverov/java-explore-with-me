package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.MapperCompilation;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationsRepository;
import ru.practicum.repository.EventsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminCompilationService {

    private final EventsRepository eventsRepository;
    private final CompilationsRepository compilationsRepository;

    public AdminCompilationService(@Autowired EventsRepository eventsRepository, CompilationsRepository compilationsRepository) {
        this.eventsRepository = eventsRepository;
        this.compilationsRepository = compilationsRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompilationDto postCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(false);
        if(newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        if(newCompilationDto.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Long eventId: newCompilationDto.getEvents()) {
                events.add(eventsRepository.getReferenceById(eventId));
            }
            compilation.setEvents(events);
        } else {
            return MapperCompilation.toCompilationDtoWithoutEvents(compilationsRepository.save(compilation));
        }
        return MapperCompilation.toCompilationDto(compilationsRepository.save(compilation));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteComp(Long compId) {
        checkComp(compId);
        compilationsRepository.deleteById(compId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        checkComp(compId);
        Compilation compilation = compilationsRepository.getReferenceById(compId);
        if(updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if(updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if(updateCompilationRequest.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Long ids: updateCompilationRequest.getEvents()) {
                events.add(eventsRepository.getReferenceById(ids));
            }
            compilation.setEvents(events);
        }
        return MapperCompilation.toCompilationDto(compilationsRepository.save(compilation));
    }

    public void checkComp(Long compId) {
        if(!compilationsRepository.existsById(compId)){
            throw new NotFoundException(String.format("Compilation with id=%s was not found", compId));
        }
    }
}
