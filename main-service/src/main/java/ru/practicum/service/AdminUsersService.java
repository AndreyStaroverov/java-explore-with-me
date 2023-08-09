package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.Users.MapperUsers;
import ru.practicum.dto.Users.UserDto;
import ru.practicum.handler.AlreadyExistEmailException;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class AdminUsersService {

    private final UserRepository userRepository;

    public AdminUsersService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Collection<UserDto> getUsers(Collection<Long> ids, Long from, Long size) {
        Pageable page = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
        if (ids != null) {
            return MapperUsers.userToDtoColl(userRepository.findAllById(ids));
        }
        return MapperUsers.userToDtoColl(userRepository.findAll(page).toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto postUser(UserDto userDto) {
        try {
            return MapperUsers.userToDto(userRepository.save(MapperUsers.dtoToUser(userDto)));
        } catch (Exception e) {
            throw new AlreadyExistEmailException("Email is used");
        }

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%s does not exist", userId));
        }
        userRepository.deleteById(userId);
    }
}
