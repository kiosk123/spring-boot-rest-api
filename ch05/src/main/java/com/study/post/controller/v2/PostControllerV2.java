package com.study.post.controller.v2;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.study.common.api.v2.V2Controller;
import com.study.common.exception.UserNotFoundException;
import com.study.post.controller.v2.dto.PostDto;
import com.study.post.service.PostService;
import com.study.user.controller.v1.dto.UserDto;
import com.study.user.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PostControllerV2 implements V2Controller {

    private final UserService userService;
    private final PostService postService;
    
    @GetMapping("/users/{userId}/posts")
    public List<PostDto> retrieveAllPostsByUser(@PathVariable("userId") Long userId) {
        Optional<UserDto> user = userService.findOneUser(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        
        return postService.getPostsByUser(userId); 
    }

    @PostMapping("/users/{userId}/posts")
    public ResponseEntity<Void> createPostByUser(@PathVariable("userId") Long userId, @RequestBody PostDto postDto) {
        postService.savePostByUser(userId, postDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri();
        
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/users/{userId}/posts")
    public ResponseEntity<Void> modifyPostByUser(@PathVariable("userId") Long userId, @RequestBody PostDto postDto) {
        postService.modifyPostByUser(userId, postDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Location", location.toString());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}/posts/{postId}")
    public ResponseEntity<Void> removePostByUser(@PathVariable("userId") Long userId,
                                                @PathVariable("postId") Long postId) {
        postService.removePostByUser(userId, postId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Location", location.toString());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
