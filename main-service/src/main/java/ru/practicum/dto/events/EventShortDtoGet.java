package ru.practicum.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDtoGet {

    @Positive
    private Long id;
    @NotBlank
    private String annotation;
    @NotNull
    private Category category;
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private Boolean paid;
    @NotBlank
    private String title;
}
