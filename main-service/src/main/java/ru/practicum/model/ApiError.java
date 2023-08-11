package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Builder
public class ApiError {

    private String status;
    private String reason;
    private String message;
    private Timestamp timestamp;

}
