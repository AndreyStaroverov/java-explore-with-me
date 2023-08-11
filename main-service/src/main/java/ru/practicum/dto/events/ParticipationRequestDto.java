package ru.practicum.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {

    @Positive
    private Long id;
    @NotNull
    private Timestamp created;
    @Positive
    private Long event;
    @NotNull
    private Long requester;
    @NotNull
    private String status;
}
