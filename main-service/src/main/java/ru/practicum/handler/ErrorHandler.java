package ru.practicum.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.model.ApiError;

import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.time.Instant;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConstrainViolationException(final ConstraintViolationException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.CONFLICT);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFound(final NotFoundException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(ConflictDateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConflictDate(final ConflictDateException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("For the requested operation the conditions are not met.")
                .message("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value:"
                        + e.getMessage())
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.CONFLICT);

    }

    @ExceptionHandler(ConflictStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConflictState(final ConflictStateException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestDate.class)
    public ResponseEntity<ApiError> handleBadRequestDate(final BadRequestDate e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Incorrectly made request String to Long.")
                .message(e.getMessage())
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AlreadyExistEmailException.class)
    public ResponseEntity<ApiError> handleEmail(final AlreadyExistEmailException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("Email already user.")
                .message("Email already used in Database")
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ResponseEntity<>(ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("Email already user.")
                .message("Conflict in Database")
                .timestamp(Timestamp.from(Instant.now()))
                .build(), HttpStatus.CONFLICT);
    }
}
