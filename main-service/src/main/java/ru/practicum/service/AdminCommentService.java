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

    public AdminCommentService(@Autowired CommentRepository commentRepository, UserRepository userRepository,
                               EventsRepository eventsRepository, CommentsCheck commentsCheck) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventsRepository = eventsRepository;
        this.commentsCheck = commentsCheck;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CommentDto getCommentById(Long comId) {
        commentsCheck.checkCom(comId);
        return MapperComments.commentDtoFromComment(commentRepository.getReferenceById(comId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Collection<CommentDto> getCommentsByUserId(Long userId) {
        commentsCheck.checkUser(userId);
        return MapperComments.commentDtoFromCommentColl(commentRepository.findAllByUserId(userId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto postComment(NewCommentDtoAdmin newCommentDto) {
        commentsCheck.checkUser(newCommentDto.getUser());
        commentsCheck.checkEvent(newCommentDto.getEvent());
        return MapperComments.commentDtoFromComment(commentRepository.save(Comment.builder()
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
        return MapperComments.commentDtoFromComment(commentRepository.save(comment));
    }
}
