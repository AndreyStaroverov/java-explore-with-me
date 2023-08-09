package ru.practicum.dto;

import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public class HitMapper {

    public static Hit dtoToHit(HitDto hitDto) {
        return new Hit(
                null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                Timestamp.valueOf(hitDto.getTimestamp())
        );
    }

    public static ViewStatsDto viewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .hits(viewStats.getHits())
                .uri(viewStats.getUri())
                .build();
    }

    public static Collection<ViewStatsDto> viewStatsDtoColl(Collection<ViewStats> viewStats) {
        Collection<ViewStatsDto> viewStatsDtos = new ArrayList<>();
        for (ViewStats viewStat : viewStats) {
            viewStatsDtos.add(
                    ViewStatsDto.builder()
                            .app(viewStat.getApp())
                            .hits(viewStat.getHits())
                            .uri(viewStat.getUri())
                            .build());
        }
        return viewStatsDtos;
    }
}
