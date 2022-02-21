package com.study.post.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.study.common.exception.PostNotFoundException;
import com.study.common.exception.UserNotFoundException;
import com.study.post.controller.v2.dto.PostDto;
import com.study.post.domain.Post;
import com.study.post.repository.PostRepository;
import com.study.user.domain.User;
import com.study.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostDto> getPostsByUser(Long userId) {
        List<Post> posts = postRepository.getPostsByUser(userId);
        return posts.stream()
                .map(p -> new PostDto(p.getId(), p.getDescription(), p.getCreateDate(), p.getUpdateDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long savePostByUser(Long userId, PostDto postDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        User findUser = user.get();
        Post post = Post.builder().description(postDto.getDescription()).user(findUser).build();
        postRepository.save(post);
        return post.getId();
    }

    @Transactional
    public Long modifyPostByUser(Long userId, PostDto postDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        User findUser = user.get();
        Optional<Post> post = postRepository.getPostByUser(findUser.getId(), postDto.getId());
        
        if(post.isEmpty()) {
            throw new PostNotFoundException(String.format("User ID[%s]\'s post ID[%s] not found", userId, postDto.getId()));
        }
        Post findPost = post.get();
        findPost.setDescription(postDto.getDescription());
        return findPost.getId();
    }

    @Transactional
    public void removePostByUser(Long userId, Long postId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        User findUser = user.get();
        Optional<Post> post = postRepository.getPostByUser(findUser.getId(), postId);
        post.ifPresent(postRepository::delete);
    }
}
