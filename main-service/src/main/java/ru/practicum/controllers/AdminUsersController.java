package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.Users.UserDto;
import ru.practicum.service.AdminUsersService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
public class AdminUsersController {

    private final AdminUsersService adminUsersService;

    public AdminUsersController(@Autowired AdminUsersService adminUsersService) {
        this.adminUsersService = adminUsersService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getUsers(@RequestParam(name = "ids", required = false) Collection<Long> ids,
                                        @RequestParam(value = "from", defaultValue = "0") @Valid @Min(0) Long from,
                                        @RequestParam(value = "size", defaultValue = "10") @Valid @Min(1) Long size) {
        return adminUsersService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@Valid @RequestBody UserDto userDto) {
        return adminUsersService.postUser(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Valid @Positive Long userId) {
        adminUsersService.deleteUser(userId);
    }
}
