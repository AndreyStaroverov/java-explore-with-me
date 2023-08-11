package ru.practicum.dto.UserRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank
    private Timestamp created;
    @NotBlank
    private Long event;
    @NotBlank
    private Long id;
    @NotBlank
    private Long requester;
    @NotBlank
    private String status;

}
