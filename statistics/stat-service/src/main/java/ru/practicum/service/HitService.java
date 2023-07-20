package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.dto.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.HitRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class HitService {

    private final HitRepository hitRepository;

    public HitService(@Autowired HitRepository hitRepository) {
        this.hitRepository = hitRepository;
    }

    public Hit saveHit(HitDto hit) {
        return hitRepository.save(HitMapper.dtoToHit(hit));
    }

    public Collection<ViewStats> getStats(String start, String end, Collection<String> uris, Boolean unique) {
        if (uris == null) {
            return emptyUris(start, end, unique);
        }
        if (unique) {
            return uniqueStats(start, end, uris, unique);
        } else {
            return notUniqueStats(start, end, uris, unique);
        }
    }

    private Collection<ViewStats> emptyUris(String start, String end, Boolean unique) {
        Collection<ViewStats> viewStats = new ArrayList<>();
        Collection<String> uris = hitRepository.findAllForEmptyUris();
        for (String string : uris) {
            viewStats.add(new ViewStats(
                    "service",
                    string,
                    hitRepository.findAllByUri(string, Timestamp.valueOf(start), Timestamp.valueOf(end))
            ));
        }
        return viewStats.stream()
                .sorted(Comparator.comparing(ViewStats::getHits))
                .collect(Collectors.toList());
    }

    public Collection<ViewStats> uniqueStats(String start, String end, Collection<String> uris, Boolean unique) {
        Collection<ViewStats> viewStats = new ArrayList<>();
        for (String string : uris) {
            viewStats.add(new ViewStats(
                    "service",
                    string,
                    hitRepository.findAllByUriUnique(string, Timestamp.valueOf(start), Timestamp.valueOf(end))
            ));
        }
        return viewStats.stream()
                .sorted(Comparator.comparing(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }

    public Collection<ViewStats> notUniqueStats(String start, String end, Collection<String> uris, Boolean unique) {
        Collection<ViewStats> viewStats = new ArrayList<>();
        for (String string : uris) {
            viewStats.add(new ViewStats(
                    "service",
                    string,
                    hitRepository.findAllByUri(string, Timestamp.valueOf(start), Timestamp.valueOf(end))
            ));
        }
        return viewStats.stream()
                .sorted(Comparator.comparing(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }
}
