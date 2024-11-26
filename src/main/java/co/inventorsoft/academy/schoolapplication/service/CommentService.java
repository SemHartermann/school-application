package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long postId, Long userId, String text);

    CommentDto addResponseToComment(Long commentId, Long userId, String text);

    List<CommentDto> getCommentsByPost(Long postId);

    CommentDto updateComment(String text, Long commentId);

    void deleteCommentById(Long postId);

    CommentDto getCommentById(Long id);
}
