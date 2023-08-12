package ru.practicum.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    @Positive
    private Long id;
    @Positive
    private Long eventId;
    @Positive
    private Long userId;
    @NotBlank
    @Size(min = 1, max = 450)
    private String text;
    @NotNull
    private Timestamp created;
}
