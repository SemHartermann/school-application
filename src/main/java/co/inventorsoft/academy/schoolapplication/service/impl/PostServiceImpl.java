package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.PostDto;
import co.inventorsoft.academy.schoolapplication.entity.ClassGroup;
import co.inventorsoft.academy.schoolapplication.entity.Post;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.exception.AccessDeniedException;
import co.inventorsoft.academy.schoolapplication.exception.PostNotFoundException;
import co.inventorsoft.academy.schoolapplication.mapper.PostMapper;
import co.inventorsoft.academy.schoolapplication.repository.ClassGroupRepository;
import co.inventorsoft.academy.schoolapplication.repository.PostRepository;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {
    PostRepository postRepository;
    PostMapper postMapper;
    ClassGroupRepository classGroupRepository;
    UserRepository userRepository;


    @Override
    public Slice<PostDto> getAllClassPosts(Long classId, Pageable pageable) throws PostNotFoundException {
        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new PostNotFoundException("ClassGroup not found with id: " + classId));
        classGroup.setId(classId);
        Slice<Post> allPostsSortedByDate = postRepository.findAllByClassGroupId(classId, pageable);
        return allPostsSortedByDate.map(postMapper::postToDto);
    }


    @Override
    public PostDto getPostById(Long id) throws PostNotFoundException {
        return postMapper.postToDto(postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id)));
    }

    @Override
    public void addNewPost(Long classId, PostDto postDto) throws PostNotFoundException {

        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new PostNotFoundException("ClassGroup not found with id: " + classId));
        User author = userRepository.findById(postDto.getAuthorId())
                .orElseThrow(() -> new PostNotFoundException("User not found with id: " + postDto.getAuthorId()));

        Post post = postMapper.postDtoToEntity(postDto);
        post.setAuthor(author);
        post.setClassGroup(classGroup);
        postRepository.save(post);
    }

    @Override
    public void updatePost(Long postId, PostDto postDto) throws AccessDeniedException, PostNotFoundException {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        User user = userRepository.findById(postDto.getAuthorId())
                .orElseThrow(() -> new PostNotFoundException("User not found with id: " + postDto.getAuthorId()));
        checkPostOwnership(postId);
        post.setPostTitle(postDto.getPostTitle());
        post.setAuthor(user);
        post.setContent(postDto.getContent());

        postRepository.save(post);

    }

    @Override
    public void deletePostById(Long postId) throws AccessDeniedException, PostNotFoundException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        checkPostOwnership(postId);
        post.setId(postId);
        postRepository.deleteById(postId);
    }

    public void checkPostOwnership(Long postId) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Access is denied");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        User currentUser = userRepository.findByEmail(userEmail).orElseThrow();

        Optional<Post> post = postRepository.findById(postId);

        if (post.isEmpty() || !post.get().getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("Access is denied because you are not the author of current post");
        }
    }
}


