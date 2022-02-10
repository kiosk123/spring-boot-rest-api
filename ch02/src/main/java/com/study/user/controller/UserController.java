package com.study.user.controller;

import java.util.List;
import java.util.Optional;

import com.study.user.dto.UserDto;
import com.study.user.dto.UserResponseDto;
import com.study.user.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

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
}
