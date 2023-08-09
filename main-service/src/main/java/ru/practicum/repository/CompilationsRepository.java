package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationsRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findAllByPinned(Boolean pinned, Pageable page);

    List<Compilation> findAllByPinned(Boolean pinned);
}
