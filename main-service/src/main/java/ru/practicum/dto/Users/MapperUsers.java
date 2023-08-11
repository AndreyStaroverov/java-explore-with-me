package ru.practicum.dto.Users;

import ru.practicum.model.User;

import java.util.ArrayList;
import java.util.Collection;

public final class MapperUsers {

    private MapperUsers() {
    }

    public static User dtoToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }

    public static UserDto userToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static Collection<UserDto> userToDtoColl(Collection<User> users) {
        Collection<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            ));
        }
        return userDtos;
    }
}
