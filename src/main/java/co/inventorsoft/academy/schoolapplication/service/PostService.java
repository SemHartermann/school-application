package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.PostDto;
import co.inventorsoft.academy.schoolapplication.exception.AccessDeniedException;
import co.inventorsoft.academy.schoolapplication.exception.PostNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostService {

    Slice<PostDto> getAllClassPosts(Long classId, Pageable pageable) throws PostNotFoundException;

    PostDto getPostById(Long id) throws PostNotFoundException;

    void addNewPost(Long classGroupId, PostDto postDto) throws PostNotFoundException;

    void updatePost(Long PostId, PostDto postDto) throws AccessDeniedException, PostNotFoundException;

    void deletePostById(Long postId) throws AccessDeniedException, PostNotFoundException;

}
