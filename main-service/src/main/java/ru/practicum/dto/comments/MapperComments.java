package ru.practicum.dto.comments;

import ru.practicum.model.Comment;

import java.util.ArrayList;
import java.util.Collection;


public final class MapperComments {

    private MapperComments() {
    }

    public static CommentDto commentDtoFromComment(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .eventId(comment.getEvent().getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static Collection<CommentDto> commentDtoFromCommentColl(Collection<Comment> comments) {
        Collection<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(CommentDto.builder()
                    .id(comment.getId())
                    .userId(comment.getUser().getId())
                    .eventId(comment.getEvent().getId())
                    .text(comment.getText())
                    .created(comment.getCreated())
                    .build());
        }
        return commentDtos;
    }
}