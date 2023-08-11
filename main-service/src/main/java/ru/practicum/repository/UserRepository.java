package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.model.User;

@Component
public interface UserRepository extends JpaRepository<User, Long> {

}
