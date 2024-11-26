package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.CommentDto;
import co.inventorsoft.academy.schoolapplication.dto.CommentRequestDto;
import co.inventorsoft.academy.schoolapplication.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;
    MockMvc mockMvc;
    @MockBean
    CommentService commentService;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void addCommentTest() throws Exception {
        Long commentId = 1L;
        Long postId = 2L;
        Long userId = 3L;
        String commentValue = "comment!";

        CommentRequestDto commentRequestDto = new CommentRequestDto(commentValue, userId);
        CommentDto expectedCommentDto = new CommentDto(commentId,
                userId,
                null,
                commentValue,
                new ArrayList<>(),
                postId);

        when(commentService.addComment(postId, userId, commentValue)).thenReturn(expectedCommentDto);

        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                ).andExpect(status().is2xxSuccessful())

                .andExpect(jsonPath("$.id").value(expectedCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(expectedCommentDto.getText()))
                .andExpect(jsonPath("$.userId").value(expectedCommentDto.getUserId()))
                .andReturn();

        verify(commentService, times(1)).addComment(postId, userId, commentValue);
    }

    @Test
    void respondToCommentTest() throws Exception {
        Long parentCommentId = 1L;
        Long responseCommentId = 2L;
        Long postId = 3L;
        Long userId = 4L;
        String responseCommentValue = "subcomment!";

        CommentRequestDto commentRequestDto = new CommentRequestDto(responseCommentValue, userId);
        CommentDto expectedResponseCommentDto = new CommentDto(responseCommentId,
                userId,
                parentCommentId,
                responseCommentValue,
                new ArrayList<>(),
                postId);

        when(commentService.addResponseToComment(parentCommentId, userId, responseCommentValue))
                .thenReturn(expectedResponseCommentDto);

        mockMvc.perform(post("/api/posts/" + postId + "/comments/" + parentCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                ).andExpect(status().is2xxSuccessful())

                .andExpect(jsonPath("$.id", Long.class).value(expectedResponseCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(expectedResponseCommentDto.getText()))
                .andExpect(jsonPath("$.userId", Long.class).value(expectedResponseCommentDto.getUserId()))
                .andExpect(jsonPath("$.parentId", Long.class).value(expectedResponseCommentDto.getParentId()))
                .andReturn();

        verify(commentService, times(1)).addResponseToComment(parentCommentId, userId, responseCommentValue);
    }

    @Test
    void getCommentsByPostTest() throws Exception {
        Long commentId = 1L;
        Long postId = 3L;
        Long userId = 4L;
        String commentValue = "comment!";
        Integer listSize = 3;
        List<CommentDto> expectedCommentDtoList = new ArrayList<>();

        for (int i = 0; i < listSize; i++) {
            expectedCommentDtoList.add(new CommentDto(commentId++,
                    userId,
                    null,
                    commentValue,
                    new ArrayList<>(),
                    postId));
        }

        when(commentService.getCommentsByPost(postId)).thenReturn(expectedCommentDtoList);

        mockMvc.perform(get("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())

                .andExpect(jsonPath("$[0].id").value(expectedCommentDtoList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(expectedCommentDtoList.get(1).getId()))
                .andExpect(jsonPath("$[2].id").value(expectedCommentDtoList.get(2).getId()))
                .andReturn();

        verify(commentService, times(1)).getCommentsByPost(postId);
    }

    @Test
    void getCommentByIdTest() throws Exception {
        Long commentId = 1L;
        Long userId = 2L;
        Long postId = 3L;
        String commentValue = "comment!";
        CommentDto expectedCommentDto = new CommentDto(commentId,
                userId,
                null,
                commentValue,
                null,
                postId);

        when(commentService.getCommentById(commentId)).thenReturn(expectedCommentDto);

        mockMvc.perform(get("/api/posts/" + postId + "/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())

                .andExpect(jsonPath("$.id", Long.class).value(expectedCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(expectedCommentDto.getText()))
                .andExpect(jsonPath("$.userId", Long.class).value(expectedCommentDto.getUserId()))
                .andExpect(jsonPath("$.parentId", Long.class).value(expectedCommentDto.getParentId()))
                .andReturn();

        verify(commentService, times(1)).getCommentById(commentId);
    }

    @Test
    void deleteCommentByIdTest() throws Exception {
        Long postId = 3L;
        Long userId = 4L;
        String text = "comment!";
        CommentDto commentDto = commentService.addComment(postId, userId, text);

        mockMvc.perform(delete("/api/posts/" + postId + "/comments/" + commentDto.getId()))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/api/posts/" + postId + "/comments/" + commentDto.getId()))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(NoSuchElementException.class));
    }
}