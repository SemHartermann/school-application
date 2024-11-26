package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.CommentDto;
import co.inventorsoft.academy.schoolapplication.entity.Comment;
import co.inventorsoft.academy.schoolapplication.entity.Post;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.repository.CommentRepository;
import co.inventorsoft.academy.schoolapplication.repository.PostRepository;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.CommentServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentServiceImplTest {

    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @InjectMocks
    CommentServiceImpl commentService;


    @Test
    void addCommentTest() {
        Long postId = 1L;
        Long userId = 2L;
        String commentValue = "comment!";

        User user = new User();
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Comment comment = commentService.addCommentReturningDomain(postId, userId, commentValue);

        assertThat(comment).isNotNull();
        assertThat(comment.getUser().getId()).isEqualTo(user.getId());
        assertThat(comment.getText()).isEqualTo(commentValue);
        assertThat(comment.getPost().getId()).isEqualTo(post.getId());

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void addResponseToCommentTest() {
        Long postId = 1L;
        Long commentId = 2L;
        Long userId = 3L;
        String responseCommentValue = "subcomment!";
        String commentValue = "comment!";

        User user = new User();
        Post post = new Post();
        post.setId(postId);
        Comment commentToResponse = new Comment(post, user, commentValue, null, new ArrayList<>());

        Comment responseComment = new Comment(post, user, responseCommentValue, commentToResponse, new ArrayList<>());
        commentToResponse.getChildrenComments().add(responseComment);
        commentToResponse.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentToResponse));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        CommentDto responseCommentDto = commentService.addResponseToComment(commentId, userId, responseCommentValue);

        assertThat(responseCommentDto).isNotNull();
        assertThat(responseCommentDto.getUserId()).isEqualTo(user.getId());
        assertThat(responseCommentDto.getText()).isEqualTo(responseComment.getText());
        assertThat(responseCommentDto.getPostId()).isEqualTo(post.getId());
        assertThat(responseCommentDto.getParentId()).isEqualTo(commentToResponse.getId());


        verify(commentRepository, times(1)).findById(commentId);
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getCommentsByPostTest() {
        Long postId = 1L;

        User user = new User();
        Post post = new Post();
        List<Comment> expectedComments = List.of(
                new Comment(post, user, "comment", null, null),
                new Comment(post, user, "another comment", null, null),
                new Comment(post, user, "one more comment", null, null)
        );

        when(commentRepository.findAllWithResponses(postId)).thenReturn(expectedComments);

        List<Comment> actualComments = commentService.getCommentsByPostReturningDomain(postId);

        assertThat(actualComments).isEqualTo(expectedComments);

        verify(commentRepository, times(1)).findAllWithResponses(postId);
    }

    @Test
    void getCommentByIdTest() {
        Long commentId = 1L;
        String commentValue = "comment!";
        User user = new User();
        Post post = new Post();
        Comment expectedComment = new Comment(post, user, commentValue, null, null);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(expectedComment));

        Comment actualComment = commentService.getCommentByIdReturningDomain(commentId);

        assertThat(actualComment).isEqualTo(expectedComment);

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void updateCommentTest() {
        Long commentId = 1L;
        Long userId = 2L;
        Long postId = 3L;
        String commentValue = "comment!";
        String newCommentValue = "new comment!";

        User user = new User();
        user.setId(userId);
        Post post = new Post();
        post.setId(postId);
        Comment oldComment = new Comment(post, user, commentValue, null, null);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(oldComment));

        CommentDto updatedComment = commentService.updateComment(newCommentValue, commentId);

        assertThat(updatedComment.getText()).isEqualTo(newCommentValue);
        assertThat(updatedComment.getUserId()).isEqualTo(oldComment.getUser().getId());
        assertThat(updatedComment.getPostId()).isEqualTo(oldComment.getPost().getId());

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void updateCommentTestFails() {
        Long commentId = 1L;
        String commentValue = "comment!";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> commentService.updateComment(commentValue, commentId));

        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void deleteCommentByIdTest() {
        Long commentId = 1L;
        Comment comment = new Comment(new Post(), new User(), "Comment", null, new ArrayList<>());
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentDto commentDto = commentService.getCommentById(commentId);
        commentService.deleteCommentById(commentDto.getId());

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> commentService.getCommentById(commentId));

        verify(commentRepository, times(2)).findById(commentId);
    }
}