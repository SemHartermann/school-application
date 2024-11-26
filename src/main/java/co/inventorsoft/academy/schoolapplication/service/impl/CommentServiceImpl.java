package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.CommentDto;
import co.inventorsoft.academy.schoolapplication.entity.Comment;
import co.inventorsoft.academy.schoolapplication.entity.Post;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.mapper.CommentMapper;
import co.inventorsoft.academy.schoolapplication.repository.CommentRepository;
import co.inventorsoft.academy.schoolapplication.repository.PostRepository;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    Integer NESTING_LEVEL_MAX = 3;

    @Transactional
    public Comment addCommentReturningDomain(Long postId, Long userId, String text) {
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Comment comment = new Comment(post, user, text, null, new ArrayList<>());
        commentRepository.save(comment);

        return comment;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long postId, Long userId, String text) {
        return commentMapper.commentToDto(addCommentReturningDomain(postId, userId, text));
    }

    @Transactional
    public Comment addResponseToCommentReturningDomain(Long commentId, Long userId, String text) {
        Comment commentToResponse = commentRepository.findById(commentId).orElseThrow();
        Comment response = addCommentReturningDomain(commentToResponse.getPost().getId(), userId, text);

        if (Objects.equals(countNestingLevel(commentToResponse), NESTING_LEVEL_MAX)) {
            response.setParentComment(commentToResponse.getParentComment());
        } else {
            response.setParentComment(commentToResponse);
        }
        return response;
    }

    @Transactional
    @Override
    public CommentDto addResponseToComment(Long commentId, Long userId, String text) {
        return commentMapper.commentToDto(addResponseToCommentReturningDomain(commentId, userId, text));
    }

    @Override
    public List<CommentDto> getCommentsByPost(Long postId) {
        List<Comment> commentList = getCommentsByPostReturningDomain(postId);
        return commentList.stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());
    }

    public List<Comment> getCommentsByPostReturningDomain(Long postId) {
        return commentRepository.findAllWithResponses(postId);
    }

    @Override
    public CommentDto getCommentById(Long id) {
        return commentMapper.commentToDto(getCommentByIdReturningDomain(id));
    }

    public Comment getCommentByIdReturningDomain(Long id) {
        return commentRepository.findById(id).orElseThrow();
    }

    @Override
    public CommentDto updateComment(String text, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setText(text);
        commentRepository.save(comment);

        return commentMapper.commentToDto(comment);
    }

    @Override
    public void deleteCommentById(Long postId) {
        commentRepository.deleteById(postId);
    }

    private int countNestingLevel(Comment comment) {
        int res = 1;

        while (comment.getParentComment() != null) {
            comment = comment.getParentComment();
            res++;
        }
        return res;
    }

}