package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.CommentDto;
import co.inventorsoft.academy.schoolapplication.dto.CommentRequestDto;
import co.inventorsoft.academy.schoolapplication.service.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("api/posts/{postId}/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    CommentService commentService;

    @PostMapping
    public CommentDto addComment(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.addComment(postId, commentRequestDto.getUserId(), commentRequestDto.getText());
    }

    @PostMapping("/{commentId}")
    public CommentDto respondToComment(@PathVariable Long commentId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.addResponseToComment(commentId, commentRequestDto.getUserId(), commentRequestDto.getText());
    }

    @GetMapping
    public List<CommentDto> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @PutMapping("/{commentId}")
    public CommentDto updateComment(@Valid @RequestBody CommentRequestDto commentRequestDto, @PathVariable Long commentId) {
        return commentService.updateComment(commentRequestDto.getText(), commentId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentById(@PathVariable Long commentId) {
        commentService.deleteCommentById(commentId);
    }
}