package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c where c.event.id = :eventId")
    List<Comment> findAllByEventId(Long eventId);

    @Query("SELECT c FROM Comment c where c.user.id = :userId")
    List<Comment> findAllByUserId(Long userId);
}
