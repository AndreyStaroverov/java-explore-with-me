package ru.practicum.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.Users.UserShortDto;
import ru.practicum.model.Category;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {

    @Positive
    private Long id;
    @NotBlank
    private String annotation;
    @NotNull
    private Category category;
    @NotNull
    private Long confirmedRequests;
    @NotBlank
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Boolean paid;
    @NotBlank
    private String title;
    @NotNull
    @Min(0)
    private Long views;
}
