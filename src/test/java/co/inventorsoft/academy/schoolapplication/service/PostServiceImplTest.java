//package co.inventorsoft.academy.schoolapplication.service;
//
//import co.inventorsoft.academy.schoolapplication.dto.PostDto;
//import co.inventorsoft.academy.schoolapplication.entity.Post;
//import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
//import co.inventorsoft.academy.schoolapplication.entity.user.Role;
//import co.inventorsoft.academy.schoolapplication.entity.user.User;
//import co.inventorsoft.academy.schoolapplication.exception.AccessDeniedException;
//import co.inventorsoft.academy.schoolapplication.exception.handler.UnauthorizedAccessException;
//import co.inventorsoft.academy.schoolapplication.mapper.PostMapper;
//import co.inventorsoft.academy.schoolapplication.repository.PostRepository;
//import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
//import co.inventorsoft.academy.schoolapplication.service.impl.PostServiceImpl;
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.*;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class PostServiceImplTest {
//
//    @InjectMocks
//    PostServiceImpl postServiceImpl;
//
//    @Mock
//    PostRepository postRepository;
//
//    @Mock
//    PostMapper postMapper;
//
//    @Mock
//    UserRepository userRepository;
//
//
////    @BeforeEach
////    public void setUp() {
////        postRepository = Mockito.mock(PostRepository.class);
////        postServiceImpl = new PostServiceImpl(postRepository, postMapper, userRepository);
////    }
//
//    @Test
//    void getAllClassPostsTest() {
//
//        Long classGroupId = 4L;
//
//        Pageable pageable = PageRequest.of(0, 10);
//        PostDto mockPostDto1 = new PostDto(1L, 2L, "Concert", "Musical eve");
//        PostDto mockPostDto2 = new PostDto(2L, 3L, "Camping", "Trip to the mountains");
//
//        Post mockPost1 = new Post();
//        mockPost1.setId(1L);
//
//        Post mockPost2 = new Post();
//        mockPost2.setId(2L);
//
//        Slice<Post> mockPostSlice = new SliceImpl<>(List.of(mockPost1, mockPost2));
//
//        when(postRepository.findAllByClassGroupId(classGroupId, pageable)).thenReturn(mockPostSlice);
//        when(postMapper.postToDto(mockPost1)).thenReturn(mockPostDto1);
//        when(postMapper.postToDto(mockPost2)).thenReturn(mockPostDto2);
//
//        Slice<PostDto> response = postServiceImpl.getAllClassPosts(classGroupId, pageable);
//
//        assertNotNull(response);
//        assertThat(response.getContent()).hasSize(2);
//        assertThat(response.getContent().get(0)).isEqualToComparingFieldByField(mockPostDto1);
//        assertThat(response.getContent().get(1)).isEqualToComparingFieldByField(mockPostDto2);
//
//        verify(postRepository, times(1)).findAllByClassGroupId(eq(classGroupId), any());
//
//    }
//
//    @Test
//    void getPostByIdTest() {
//
//        Long postId = 2L;
//        Long userId = 3L;
//        String title = "Java";
//        String content = "The best programming language";
//
//        Post mockPost = new Post();
//        mockPost.setId(postId);
//        mockPost.setPostTitle(title);
//        mockPost.setContent(content);
//
//        PostDto mockPostDto = new PostDto(postId, userId, title, content);
//
//        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
//        when(postMapper.postToDto(mockPost)).thenReturn(mockPostDto);
//
//        PostDto actualResponse = postServiceImpl.getPostById(postId);
//
//        assertNotNull(actualResponse);
//        assertThat(actualResponse.getId()).isEqualTo(postId);
//        assertThat(actualResponse.getAuthorId()).isEqualTo(userId);
//        assertThat(actualResponse.getPostTitle()).isEqualTo(title);
//        assertThat(actualResponse.getContent()).isEqualTo(content);
//
//        verify(postRepository, times(1)).findById(postId);
//    }
//    @Test
//    void addNewPostTest() {
//
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("komk@gnail.con");
//        user.setRole(Role.TEACHER);
//        user.setPassword("0109K");
//        user.setAccountStatus(AccountStatus.ACTIVE);
//
//        Long postDtoId = 1L;
//        Long userId = 1L;
//        String title = "New Year";
//        String content = "Christmas Holidays!";
//
//        PostDto postDto = new PostDto(postDtoId, userId, title, content);
//
//        Post mockPost = new Post();
//        mockPost.setId(postDtoId);
//        mockPost.setAuthor(user);
//        mockPost.setPostTitle(title);
//        mockPost.setContent(content);
//
//        when(postMapper.postDtoToEntity(postDto)).thenReturn(mockPost);
//        when(postRepository.save(mockPost)).thenReturn(mockPost);
//
//        postServiceImpl.addNewPost(userId, postDto);
//
//        verify(postMapper, times(1)).postDtoToEntity(postDto);
//        verify(postRepository, times(1)).save(any());
//
//        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
//        verify(postRepository).save(postCaptor.capture());
//        Post savedPost = postCaptor.getValue();
//        assertNotNull(savedPost);
//        assertThat(savedPost.getAuthor().getEmail()).isEqualTo(user.getEmail());
//        assertThat(savedPost.getPostTitle()).isEqualTo(title);
//        assertThat(savedPost.getContent()).isEqualTo(content);
//    }
////    @Test
////    void updatePost(){
////        Long postId = 2L;
////        Long userId = 3L;
////        String title = "New Year";
////        String content = "Christmas Holidays!";
////
////        PostDto requestPostDto = new PostDto(postId, userId, title, content);
////
////        Post post = new Post();
////
////        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
////        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
////
////        postServiceImpl.updatePost(postId, requestPostDto);
////
////        assertNotNull(requestPostDto);
////        assertThat(requestPostDto.getId()).isEqualTo(postId);
////        assertThat(requestPostDto.getAuthorId()).isEqualTo(userId);
////        assertThat(requestPostDto.getPostTitle()).isEqualTo(title);
////        assertThat(requestPostDto.getContent()).isEqualTo(content);
////
////        verify(postRepository, times(1)).findById(postId);
////
////        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
////        verify(postRepository, times(1)).save(postCaptor.capture());
////
////        Post savedPost = postCaptor.getValue();
////        assertNotNull(savedPost);
////
////    }
//
//    @Test
//    void deletePostById() throws AccessDeniedException {
//
//        Long postId = 1L;
//
//        Post post = new Post();
//
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//
//        postServiceImpl.deletePostById(postId);
//
//        verify(postRepository, times(1)).deleteById(postId);
//
//    }
//}
