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

    /**
     * @Valid 어노테이션 추가하여 밸리데이션 활성화
     */
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserRequestDto userRequestDto) {
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

    /**
     * @Valid 어노테이션 추가하여 밸리데이션 활성화
     */
    @PutMapping("/users")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        Optional<UserDto> findUser = userService.updateUser(userRequestDto);
        if (findUser.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userRequestDto.getId()));
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(userRequestDto.getId())
            .toUri();
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        
        return new ResponseEntity<Void>(responseHeaders, HttpStatus.OK);
    }
}
