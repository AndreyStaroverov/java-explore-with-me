package ru.practicum.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictDateException extends RuntimeException {
    public ConflictDateException(String message) {
        super(message);
    }
}
