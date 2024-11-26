package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ReactionDto;
import co.inventorsoft.academy.schoolapplication.entity.enums.ReactionType;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.service.ReactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionController {
    ReactionService reactionService;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionDto addReaction(@PathVariable Long postId,
                                   @RequestBody ReactionType reactionType,
                                   @AuthenticationPrincipal User user) {

        return reactionService.addReaction(postId, user.getId(), reactionType);
    }

    @GetMapping("/{postId}/count")
    public Map<ReactionType, Long> countReactions(@PathVariable Long postId) {
        return reactionService.countReaction(postId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReaction(@PathVariable Long postId,
                               @AuthenticationPrincipal User user) {

        reactionService.removeReaction(postId, user.getId());
    }
}