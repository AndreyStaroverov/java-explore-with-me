package ru.practicum.dto.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank
    @Size(min = 2, message = "{validation.name.size.too_short}")
    @Size(max = 250, message = "{validation.name.size.too_long}")
    private String name;
    @NotBlank
    @Size(min = 6, message = "{validation.name.size.too_short}")
    @Size(max = 254, message = "{validation.name.size.too_long}")
    @Email
    private String email;

}
