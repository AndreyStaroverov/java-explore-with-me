package ru.practicum.dto;

import ru.practicum.model.Hit;

import java.sql.Timestamp;

public class HitMapper {

    public static Hit DtoToHit(HitDto hitDto) {
        return new Hit(
                null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                Timestamp.valueOf(hitDto.getTimestamp())
        );
    }
}
