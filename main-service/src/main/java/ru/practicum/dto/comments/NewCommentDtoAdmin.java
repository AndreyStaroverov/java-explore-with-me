package ru.practicum.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDtoAdmin {

    @NotBlank
    @Size(min = 1, max = 450)
    private String text;
    @NotNull
    private Long event;
    @NotNull
    private Long user;
}
