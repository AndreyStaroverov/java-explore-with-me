package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comments.*;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Service
public class AdminCommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    private final CommentsCheck commentsCheck;
    private final MapperComments mapperComments;

    public AdminCommentService(@Autowired CommentRepository commentRepository, UserRepository userRepository,
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
    public Collection<CommentDto> getCommentsByUserId(Long userId) {
        commentsCheck.checkUser(userId);
        return mapperComments.commentDtoFromCommentColl(commentRepository.findAllByUserId(userId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto postComment(NewCommentDtoAdmin newCommentDto) {
        commentsCheck.checkUser(newCommentDto.getUser());
        commentsCheck.checkEvent(newCommentDto.getEvent());
        return mapperComments.commentDtoFromComment(commentRepository.save(Comment.builder()
                .user(userRepository.getReferenceById(newCommentDto.getUser()))
                .text(newCommentDto.getText())
                .event(eventsRepository.getReferenceById(newCommentDto.getEvent()))
                .created(Timestamp.from(Instant.now()))
                .build()));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteComment(Long comId) {
        commentsCheck.checkCom(comId);
        commentRepository.deleteById(comId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto updateCommentByAdmin(Long comId, NewCommentDto newCommentDto) {
        commentsCheck.checkCom(comId);
        Comment comment = commentRepository.getReferenceById(comId);
        comment.setText(newCommentDto.getText());
        return mapperComments.commentDtoFromComment(commentRepository.save(comment));
    }
}
