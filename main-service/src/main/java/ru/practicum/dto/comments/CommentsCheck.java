package ru.practicum.dto.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.enums.EventStates;
import ru.practicum.handler.NotFoundException;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.UserRepository;

@Component
public class CommentsCheck {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    public CommentsCheck(@Autowired CommentRepository commentRepository, UserRepository userRepository,
                         EventsRepository eventsRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventsRepository = eventsRepository;
    }

    public void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%s not exist", userId));
        }
    }

    public void checkEvent(Long eventId) {
        if (!eventsRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%s not exist", eventId));
        }
        if (!eventsRepository.getReferenceById(eventId).getState().equals(EventStates.PUBLISHED.toString())) {
            throw new NotFoundException(String.format("Event with id=%s not PUBLISHED", eventId));
        }
    }

    public void checkCom(Long comId) {
        if (!commentRepository.existsById(comId)) {
            throw new NotFoundException(String.format("Comment with id=%s not exist", comId));
        }
    }
}
