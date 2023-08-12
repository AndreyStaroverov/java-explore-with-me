package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.CommentsCheck;
import ru.practicum.dto.comments.MapperComments;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.handler.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.UserRepository;

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
    private final CommentsCheck commentsCheck;
    private final MapperComments mapperComments;

    public UserCommentService(@Autowired CommentRepository commentRepository, UserRepository userRepository,
                              EventsRepository eventsRepository, CommentsCheck commentsCheck, MapperComments mapperComments) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventsRepository = eventsRepository;
        this.commentsCheck = commentsCheck;
        this.mapperComments = mapperComments;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CommentDto getCommentById(Long comId) {
        commentsCheck.checkCom(comId);
        return mapperComments.commentDtoFromComment(commentRepository.getReferenceById(comId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<CommentDto> getCommentsByEventId(Long eventId) {
        commentsCheck.checkEvent(eventId);
        return mapperComments
                .commentDtoFromCommentColl(commentRepository.findAllByEventId(eventId))
                .stream().sorted(Comparator.comparing(CommentDto::getCreated))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<CommentDto> getCommentsByUserId(Long userId) {
        commentsCheck.checkUser(userId);
        return mapperComments
                .commentDtoFromCommentColl(commentRepository.findAllByUserId(userId))
                .stream().sorted(Comparator.comparing(CommentDto::getCreated))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto postCommentByUser(Long userId, Long eventId, NewCommentDto newCommentDto) {
        commentsCheck.checkUser(userId);
        commentsCheck.checkEvent(eventId);
        return mapperComments.commentDtoFromComment(commentRepository.save(Comment.builder()
                .user(userRepository.getReferenceById(userId))
                .created(Timestamp.from(Instant.now()))
                .event(eventsRepository.getReferenceById(eventId))
                .text(newCommentDto.getText()).build())
        );
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto updateCommentByUser(Long comId, Long userId, NewCommentDto newCommentDto) {
        commentsCheck.checkCom(comId);
        commentsCheck.checkUser(userId);
        if (!commentRepository.getReferenceById(comId).getUser().getId().equals(userId)) {
            throw new NotFoundException(String.format("This comment author is not id=%s", userId));
        }
        Comment comment = commentRepository.getReferenceById(comId);
        comment.setText(newCommentDto.getText());
        return mapperComments.commentDtoFromComment(commentRepository.save(comment));
    }

}
