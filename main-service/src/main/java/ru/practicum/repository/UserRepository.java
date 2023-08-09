package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.practicum.model.User;

import java.util.List;

@Component
public interface UserRepository extends JpaRepository<User, Long> {

}
