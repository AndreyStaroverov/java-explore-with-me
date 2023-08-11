package ru.practicum.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.Users.UserShortDto;
import ru.practicum.model.Category;
import ru.practicum.model.Location;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {

    @Positive
    private Long id;
    @NotBlank
    private String annotation;
    @NotNull
    private Category category;
    @NotNull
    private Long confirmedRequests;
    @NotNull
    private String createdOn;
    @NotBlank
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    @NotNull
    @Min(0)
    private Integer participantLimit;
    @NotNull
    private String publishedOn;
    @NotNull
    private Boolean requestModeration;
    @NotBlank
    private String state;
    @NotBlank
    private String title;
    @Min(0)
    @NotNull
    private Long views;
}
