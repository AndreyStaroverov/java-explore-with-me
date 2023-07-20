package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {

    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    private String timestamp;
}