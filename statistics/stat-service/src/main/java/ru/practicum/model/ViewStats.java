package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {

    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @Positive
    private Long hits;
}
