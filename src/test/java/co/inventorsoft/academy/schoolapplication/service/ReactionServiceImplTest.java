package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.ReactionDto;
import co.inventorsoft.academy.schoolapplication.entity.Post;
import co.inventorsoft.academy.schoolapplication.entity.Reaction;
import co.inventorsoft.academy.schoolapplication.entity.enums.ReactionType;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.repository.PostRepository;
import co.inventorsoft.academy.schoolapplication.repository.ReactionRepository;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.ReactionServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionServiceImplTest {
    @Mock
    ReactionRepository reactionRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ReactionServiceImpl reactionServiceImpl;

    @Test
    public void addReactionTest() {
        Long postId = 1L;
        Long userId = 1L;
        ReactionType reactionType = ReactionType.LIKE;

        Post post = new Post();
        User user = new User();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ReactionDto reactionDto = reactionServiceImpl.addReaction(postId, userId, reactionType);

        assertThat(reactionDto).isNotNull();
        assertThat(reactionDto.getReactionType()).isEqualTo(reactionType);
        assertThat(reactionDto.getPostId()).isEqualTo(postId);
        assertThat(reactionDto.getUserId()).isEqualTo(userId);

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void countReactionTest() {
        Long postId = 1L;
        Post post = new Post();
        List<Reaction> reactions = new ArrayList<>();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reactionRepository.findByPost(post)).thenReturn(reactions);

        Map<ReactionType, Long> result = reactionServiceImpl.countReaction(postId);

        assertThat(result).isNotNull();
        for (ReactionType type : ReactionType.values()) {
            assertThat(result).contains(entry(type, 0L));
        }

        verify(postRepository, times(1)).findById(postId);
        verify(reactionRepository, times(1)).findByPost(post);
    }

    @Test
    public void removeReactionTest() {
        Long postId = 1L;
        Long userId = 1L;

        Post post = new Post();
        User user = new User();
        Reaction reaction = new Reaction();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reactionRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(reaction));

        reactionServiceImpl.removeReaction(postId, userId);

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findById(userId);
        verify(reactionRepository, times(1)).delete(reaction);
    }
}