package ru.practicum.dto;

import ru.practicum.HitDto;
import ru.practicum.model.Hit;

import java.sql.Timestamp;

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
}
