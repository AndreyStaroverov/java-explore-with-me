package ru.practicum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.dto.comments.NewCommentDtoAdmin;
import ru.practicum.service.AdminCommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping(path = "/admin/comments")
@Validated
public class AdminCommentsController {

    private final AdminCommentService adminCommentService;

    public AdminCommentsController(@Autowired AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @GetMapping("/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable @Positive Long comId) {
        return adminCommentService.getCommentById(comId);
    }

    @GetMapping("/{userId}/users")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> getUserComments(@PathVariable @Positive Long userId) {
        return adminCommentService.getCommentsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@RequestBody @Valid NewCommentDtoAdmin newCommentDto) {
        return adminCommentService.postComment(newCommentDto);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Valid @Positive Long comId) {
        adminCommentService.deleteComment(comId);
    }

    @PatchMapping("/{comId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentByUser(@PathVariable @Positive Long comId,
                                          @RequestBody @Valid NewCommentDto newCommentDto) {
        return adminCommentService.updateCommentByAdmin(comId, newCommentDto);
    }
}
