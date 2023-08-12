package ru.practicum.dto.comments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.Comment;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface MapperComments {
    @Mapping(target = "userId", source = "comment.user.id")
    @Mapping(target = "eventId", source = "comment.event.id")
    CommentDto commentDtoFromComment(Comment comment);

    @Mapping(target = "userId", source = "comment.user.id")
    @Mapping(target = "eventId", source = "comment.event.id")
    Collection<CommentDto> commentDtoFromCommentColl(Collection<Comment> comments);
}