package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.ReactionDto;
import co.inventorsoft.academy.schoolapplication.entity.enums.ReactionType;

import java.util.Map;

public interface ReactionService {
    ReactionDto addReaction(Long postId, Long userId, ReactionType reactionType);

    Map<ReactionType, Long> countReaction(Long postId);

    void removeReaction(Long postId, Long userId);
}