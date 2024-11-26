package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.PostDto;
import co.inventorsoft.academy.schoolapplication.exception.AccessDeniedException;
import co.inventorsoft.academy.schoolapplication.exception.PostNotFoundException;
import co.inventorsoft.academy.schoolapplication.exception.handler.UnauthorizedAccessException;
import co.inventorsoft.academy.schoolapplication.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {

    PostService postService;

    @GetMapping("/class/{classId}")
    public Slice<PostDto> getAllClassPosts(@PathVariable("classId") Long classId,
                                           @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) throws PostNotFoundException {
        return postService.getAllClassPosts(classId, pageable);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable("id") Long id) throws PostNotFoundException {
        return postService.getPostById(id);
    }

    @PostMapping("/class/{classGroupId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN')")
    public void addNewPost( @PathVariable Long classGroupId,
            @NotNull @RequestBody PostDto postDto) throws PostNotFoundException {
        postService.addNewPost(classGroupId, postDto);
    }

    @PutMapping("{postId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN')")
    public void updatePostById(@PathVariable("postId") Long postId,
                               @RequestBody PostDto postDto) throws AccessDeniedException, PostNotFoundException {
        postService.updatePost(postId, postDto);
    }

    @DeleteMapping("{postId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN')")
    public void deleteSubjectById(@PathVariable("postId") Long postId) throws AccessDeniedException, PostNotFoundException {
        postService.deletePostById(postId);
    }
}
