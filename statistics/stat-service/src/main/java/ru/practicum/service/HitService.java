package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.HitMapper;
import ru.practicum.handler.BadRequestException;
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

    public Collection<ViewStatsDto> getStats(String start, String end, String[] uris, Boolean unique) {
        if (Timestamp.valueOf(start).after(Timestamp.valueOf(end))) {
            throw new BadRequestException("Date start before Date end");
        }
        if (uris == null) {
            return HitMapper.viewStatsDtoColl(emptyUris(start, end, unique));
        }
        if (unique) {
            return HitMapper.viewStatsDtoColl(uniqueStats(start, end, uris, unique));
        }
        return HitMapper.viewStatsDtoColl(notUniqueStats(start, end, uris, unique));

    }

    private Collection<ViewStats> emptyUris(String start, String end, Boolean unique) {
        Collection<ViewStats> viewStats = new ArrayList<>();
        Collection<String> uris = hitRepository.findAllForEmptyUris();
        for (String uri : uris) {
            viewStats.add(new ViewStats(
                    "service",
                    uri,
                    hitRepository.findAllByUri(uri, Timestamp.valueOf(start), Timestamp.valueOf(end))
            ));
        }
        return viewStats.stream()
                .sorted(Comparator.comparing(ViewStats::getHits))
                .collect(Collectors.toList());
    }

    public Collection<ViewStats> uniqueStats(String start, String end, String[] uris, Boolean unique) {
        Collection<ViewStats> viewStats = new ArrayList<>();
        for (String uri : uris) {
            viewStats.add(new ViewStats(
                    "service",
                    uri,
                    hitRepository.findAllByUriUnique(uri, Timestamp.valueOf(start), Timestamp.valueOf(end))
            ));
        }
        return viewStats.stream()
                .sorted(Comparator.comparing(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }

    public Collection<ViewStats> notUniqueStats(String start, String end, String[] uris, Boolean unique) {
        Collection<ViewStats> viewStats = new ArrayList<>();
        for (String uri : uris) {
            viewStats.add(new ViewStats(
                    "service",
                    uri,
                    hitRepository.findAllByUri(uri, Timestamp.valueOf(start), Timestamp.valueOf(end))
            ));
        }
        return viewStats.stream()
                .sorted(Comparator.comparing(ViewStats::getHits).reversed())
                .collect(Collectors.toList());
    }
}
