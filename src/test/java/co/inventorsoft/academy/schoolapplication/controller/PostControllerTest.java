////package co.inventorsoft.academy.schoolapplication.controller;
////
////import co.inventorsoft.academy.schoolapplication.SchoolApplication;
////import co.inventorsoft.academy.schoolapplication.dto.PostDto;
////import co.inventorsoft.academy.schoolapplication.exception.handler.UnauthorizedAccessException;
////import co.inventorsoft.academy.schoolapplication.service.PostService;
////import com.fasterxml.jackson.databind.ObjectMapper;
////import lombok.AccessLevel;
////import lombok.experimental.FieldDefaults;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.mockito.InjectMocks;
////import org.mockito.MockitoAnnotations;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
////import org.springframework.boot.test.context.SpringBootTest;
////import org.springframework.boot.test.mock.mockito.MockBean;
////import org.springframework.data.domain.Pageable;
////import org.springframework.data.domain.Slice;
////import org.springframework.data.domain.SliceImpl;
////import org.springframework.http.MediaType;
////import org.springframework.test.web.servlet.MockMvc;
////import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
////import org.springframework.test.web.servlet.setup.MockMvcBuilders;
////
////import java.util.List;
////
////import static org.hamcrest.Matchers.hasSize;
////import static org.mockito.ArgumentMatchers.any;
////import static org.mockito.Mockito.*;
////import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
////import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
////
////@SpringBootTest(classes = SchoolApplication.class)
////@AutoConfigureMockMvc
////@FieldDefaults(level = AccessLevel.PRIVATE)
////public class PostControllerTest {
////
////    @Autowired
////    MockMvc mockMvc;
////
////    @Autowired
////    ObjectMapper objectMapper;
////
////    @InjectMocks
////    PostController postController;
////
////    @MockBean
////    PostService postService;
////
////    @BeforeEach
////    void init() {
////        MockitoAnnotations.openMocks(this);
////        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
////    }
////
////    @Test
////    void getAllClassPostsTest() throws Exception {
////        Long classId = 4L;
////        Slice<PostDto> page = new SliceImpl<>(List.of(
////                new PostDto(1L, 2L, "Java", "Programming lesson"),
////                new PostDto(2L, 3L, "Python", "Programming lesson 2")
////        ));
////
////        when(postService.getAllClassPosts(classId, any(Pageable.class))).thenReturn(page);
////
////        mockMvc.perform(get("/api/posts/class/", classId))
////                .andExpect(status().isOk())
////                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)));
////
////        verify(postService).getAllClassPosts(classId, any(Pageable.class));
////
////    }
////
////    @Test
////    void getPostById() throws Exception {
////        Long postId = 1L;
////
////        PostDto postDto = new PostDto(postId, 2L, "Java", "Programming language");
////
////        when(postService.getPostById(postId)).thenReturn(postDto);
////
////        mockMvc.perform(get("/api/posts/{postId}", postId))
////                .andExpect(status().isOk())
////                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postDto.getId()));
////
////        verify(postService).getPostById(postId);
////
////    }
////
////    @Test
////    void addNewPost() throws Exception, UnauthorizedAccessException {
////        Long postId = 1L;
////        Long userId = 2L;
////        String title = "Java";
////        String content = "Programming language";
////
////        PostDto postDto = new PostDto(postId, userId, title, content);
////
////        doNothing().when(postService).addNewPost(userId, postDto);
////
////
////        mockMvc.perform(post("/api/posts")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(postDto)))
////                .andExpect(status().isCreated())
////                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postDto.getId()));
////
////        verify(postService).addNewPost(userId, postDto);
////
////    }
//
////    @Test
////    void updatePostById() throws UnauthorizedAccessException, Exception {
////        Long postId = 1L;
////        Long userId = 2L;
////        String title = "Java";
////        String content = "Programming language";
////
////        PostDto postDto = new PostDto(postId, userId, title, content);
////        doNothing().when(postService).updatePost(userId, postId, postDto);
////
////        mockMvc.perform(put("/api/posts/{userId}/{postId}", userId, postId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(postDto)))
////                .andExpect(status().isOk())
////                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postDto.getId()))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(postDto.getAuthorId()));
////
////        verify(postService).updatePost(userId, postId, postDto);
////
////    }
//
////    @Test
////    void deletePostById() throws UnauthorizedAccessException, Exception {
////        Long postId = 1L;
////        Long userId = 2L;
////
////        doNothing().when(postService).deletePostById(userId, postId);
////
////        mockMvc.perform(delete("/api/posts/{userId}/{postId}", userId, postId))
////                .andExpect(status().isNoContent());
////
////
////        verify(postService).deletePostById(userId, postId);
////    }
//}
