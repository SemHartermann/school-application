package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ReactionDto;
import co.inventorsoft.academy.schoolapplication.entity.enums.ReactionType;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.service.ReactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ReactionService reactionService;

    @Test
    @WithMockUser
    public void addReactionTest() throws Exception {
        Long postId = 1L;
        ReactionType reactionType = ReactionType.LIKE;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
        SecurityContextHolder.setContext(securityContext);

        ReactionDto reactionDto = new ReactionDto();
        reactionDto.setPostId(postId);
        reactionDto.setUserId(userId);
        reactionDto.setReactionType(reactionType);

        when(reactionService.addReaction(postId, userId, reactionType)).thenReturn(reactionDto);

        mockMvc.perform(post("/api/reactions/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactionType)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postId", is(postId.intValue())))
                .andExpect(jsonPath("$.userId", is(userId.intValue())))
                .andExpect(jsonPath("$.reactionType", is(reactionType.toString())));

        verify(reactionService, times(1)).addReaction(postId, userId, reactionType);
    }


    @Test
    @WithMockUser
    public void countReactionsTest() throws Exception {
        Long postId = 1L;
        Map<ReactionType, Long> reactionsCount = new HashMap<>();
        reactionsCount.put(ReactionType.LIKE, 1L);

        when(reactionService.countReaction(postId)).thenReturn(reactionsCount);

        String response = mockMvc.perform(get("/api/reactions/" + postId + "/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).isEqualTo("{\"LIKE\":1}");

        verify(reactionService, times(1)).countReaction(postId);
    }

    @Test
    @WithMockUser
    public void removeReactionTest() throws Exception {
        Long postId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
        SecurityContextHolder.setContext(securityContext);

        doNothing().when(reactionService).removeReaction(postId, userId);

        mockMvc.perform(delete("/api/reactions/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(reactionService, times(1)).removeReaction(postId, userId);
    }

}