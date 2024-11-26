package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.ReactionDto;
import co.inventorsoft.academy.schoolapplication.entity.Post;
import co.inventorsoft.academy.schoolapplication.entity.Reaction;
import co.inventorsoft.academy.schoolapplication.entity.enums.ReactionType;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.exception.ResourceNotFoundException;
import co.inventorsoft.academy.schoolapplication.mapper.ReactionMapper;
import co.inventorsoft.academy.schoolapplication.repository.PostRepository;
import co.inventorsoft.academy.schoolapplication.repository.ReactionRepository;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.ReactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReactionServiceImpl implements ReactionService {

    ReactionRepository reactionRepository;
    UserRepository userRepository;
    PostRepository postRepository;

    ReactionMapper reactionMapper = Mappers.getMapper(ReactionMapper.class);

    @Transactional
    @Override
    public ReactionDto addReaction(Long postId, Long userId, ReactionType reactionType) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException("User not found"));


        Optional<Reaction> optionalExistingReaction = reactionRepository.findByPostAndUser(post, user);

        if (optionalExistingReaction.isPresent()) {
            Reaction existingReaction = optionalExistingReaction.get();
            existingReaction.setReactionType(reactionType);
            existingReaction.setPost(post);

            log.info("Updated reaction on post {} for user {} ", postId, userId);

            ReactionDto reactionDto = reactionMapper.reactionToDto(existingReaction);
            reactionDto.setPostId(postId);
            reactionDto.setUserId(userId);
            return reactionDto;

        } else {
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setReactionType(reactionType);

            reactionRepository.save(reaction);

            log.info("Added new reaction on post {} for user {} ", postId, userId);

            ReactionDto reactionDto = reactionMapper.reactionToDto(reaction);
            reactionDto.setPostId(postId);
            reactionDto.setUserId(userId);
            return reactionDto;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<ReactionType, Long> countReaction(Long postId) {
        log.info("Counting reactions for post {}", postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        List<Reaction> reactions = reactionRepository.findByPost(post);

        Map<ReactionType, Long> reactionsCounts = reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReactionType, Collectors.counting()));

        for (ReactionType type : ReactionType.values()) {
            reactionsCounts.putIfAbsent(type, 0L);
        }

        log.info("Counted {} reactions for post {}", reactionsCounts.size(), postId);

        return reactionsCounts;
    }

    @Transactional
    @Override
    public void removeReaction(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException("User not found"));

        Optional<Reaction> optionalExistingReaction = reactionRepository.findByPostAndUser(post, user);

        if (optionalExistingReaction.isPresent()) {
            reactionRepository.delete(optionalExistingReaction.get());
            log.info("Deleted reaction on post {} for user {} ", postId, userId);
        } else {
            log.info("No reaction found on post {} for user {} ", postId, userId);
        }
    }

}