package com.study.user.controller.v2;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.study.common.api.v2.V2Controller;
import com.study.common.exception.UserNotFoundException;
import com.study.user.domain.User;
import com.study.user.dto.UserDto;
import com.study.user.dto.UserRequestDto;
import com.study.user.service.UserService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserControllerV2 implements V2Controller {
    
    private final UserService userService;

    @GetMapping("/users")
    public List<UserDto> retrieveAllUsers() {
        List<UserDto> users = userService.findAll();
        return users;
    }

    @GetMapping("/users/{id}")
    public UserDto retrieveUser(@PathVariable("id") Long id) {
        Optional<UserDto> user = userService.findOneUser(id);
        if (user.isPresent()) {
           return user.get();
        }
        throw new UserNotFoundException(String.format("ID[%s] not found", id));
    }

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = User.builder()
            .name(userRequestDto.getName())
            .build();
        Long id = userService.saveUser(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}") 
            .buildAndExpand(id)
            .toUri();
        
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void removeUser(@PathVariable("id") Long id) {
        userService.removeUser(id);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable("id") Long id, @Req) {
        Optional<UserDto> findUser = userService.updateUser(id);
        if (findUser.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", id));
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .buildAndExpand(id)
            .toUri();
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        
        return new ResponseEntity<Void>(responseHeaders, HttpStatus.OK);
    }
}
