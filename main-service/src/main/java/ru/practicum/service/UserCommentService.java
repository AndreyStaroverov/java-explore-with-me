package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.MapperComments;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.enums.EventStates;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.UserRepository;

import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class UserCommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    public UserCommentService(@Autowired CommentRepository commentRepository, UserRepository userRepository,
                              EventsRepository eventsRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventsRepository = eventsRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CommentDto getCommentById(Long comId) {
        checkCom(comId);
        return MapperComments.commentDtoFromComment(commentRepository.getReferenceById(comId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<CommentDto> getCommentsByEventId(Long eventId) {
        checkEvent(eventId);
        return MapperComments
                .commentDtoFromCommentColl(commentRepository.findAllByEventId(eventId))
                .stream().sorted(Comparator.comparing(CommentDto::getCreated))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<CommentDto> getCommentsByUserId(Long userId) {
        checkUser(userId);
        return MapperComments
                .commentDtoFromCommentColl(commentRepository.findAllByUserId(userId))
                .stream().sorted(Comparator.comparing(CommentDto::getCreated))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto postCommentByUser(Long userId, Long eventId, NewCommentDto newCommentDto) {
        checkUser(userId);
        checkEvent(eventId);
        return MapperComments.commentDtoFromComment(commentRepository.save(Comment.builder()
                .user(userRepository.getReferenceById(userId))
                .created(Timestamp.from(Instant.now()))
                .event(eventsRepository.getReferenceById(eventId))
                .text(newCommentDto.getText()).build())
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto updateCommentByUser(Long comId, Long userId, NewCommentDto newCommentDto) {
        checkCom(comId);
        checkUser(userId);
        if (!commentRepository.getReferenceById(comId).getUser().getId().equals(userId)) {
            throw new NotFoundException(String.format("This comment author is not id=%s", userId));
        }
        Comment comment = commentRepository.getReferenceById(comId);
        comment.setText(newCommentDto.getText());
        return MapperComments.commentDtoFromComment(commentRepository.save(comment));
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
