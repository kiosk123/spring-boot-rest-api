package com.study.user.controller.v1;

import java.util.List;
import java.util.Optional;

import com.study.common.api.v1.V1Controller;
import com.study.user.domain.User;
import com.study.user.dto.UserDto;
import com.study.user.dto.UserRequestDto;
import com.study.user.dto.UserResponseDto;
import com.study.user.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserControllerV1 implements V1Controller {

    private final UserService userService;

    @GetMapping("/users")
    public UserResponseDto retrieveAllUsers() {
        List<UserDto> users = userService.findAll();
        
        if (!users.isEmpty()) {
            UserResponseDto response = new UserResponseDto();
            response.setUsers(users);
            return response;
        }

        return new UserResponseDto(HttpStatus.NOT_FOUND.value(), "User list is empty");
    }

    @GetMapping("/users/{id}")
    public UserResponseDto retrieveUser(@PathVariable("id") Long id) {
        Optional<UserDto> user = userService.findOneUser(id);
        if (user.isPresent()) {
            UserResponseDto response = new UserResponseDto();
            response.setUser(user.get());
            return response;
        }
        return new UserResponseDto(HttpStatus.NOT_FOUND.value(), "User is not found");
    }

    /**
     * RestController에서 POST처리시 @RequestBody는 필수
     */
    @PostMapping("/users")
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        User user = User.builder()
            .name(userRequestDto.getName())
            .build();
        Long userId = userService.saveUser(user);
        Optional<UserDto> findUser = userService.findOneUser(userId);
        
        if (findUser.isPresent()) {
            UserResponseDto response = new UserResponseDto();
            response.setUser(findUser.get());
            return response;
        }
        return new UserResponseDto(HttpStatus.BAD_REQUEST.value(), "Registering new user is failed");
    }

    @DeleteMapping("/users/{id}")
    public UserResponseDto removeUser(@PathVariable("id") Long id) {
        userService.removeUser(id);
        return new UserResponseDto();
    }
}
