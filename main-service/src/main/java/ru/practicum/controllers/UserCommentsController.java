package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.service.UserCommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserCommentsController {

    private final UserCommentService userCommentService;

    public UserCommentsController(@Autowired UserCommentService userCommentService) {
        this.userCommentService = userCommentService;
    }

    @GetMapping("/comments/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> getEventComments(@PathVariable @Positive Long eventId) {
        return userCommentService.getCommentsByEventId(eventId);
    }

    @GetMapping("/comments/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable @Positive Long comId) {
        return userCommentService.getCommentById(comId);
    }

    @GetMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> getUserComments(@PathVariable @Positive Long userId) {
        return userCommentService.getCommentsByUserId(userId);
    }

    @PostMapping("/{userId}/comments/event/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long eventId,
                                  @RequestBody @Valid NewCommentDto newCommentDto) {
        return userCommentService.postCommentByUser(userId, eventId, newCommentDto);
    }

    @PatchMapping("{userId}/comments/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentByUser(@PathVariable @Positive Long comId,
                                          @PathVariable @Positive Long userId,
                                          @RequestBody @Valid NewCommentDto newCommentDto) {
        return userCommentService.updateCommentByUser(comId, userId, newCommentDto);
    }
}
