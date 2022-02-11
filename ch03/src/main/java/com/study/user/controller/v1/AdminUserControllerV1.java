package com.study.user.controller.v1;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.study.common.api.v1.V1Controller;
import com.study.common.exception.UserNotFoundException;
import com.study.user.controller.v1.dto.UserDto;
import com.study.user.service.UserService;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AdminUserControllerV1 implements V1Controller {

    private final UserService userService;

    @GetMapping("/admin/users")
    public MappingJacksonValue retrieveAllUsers() {
        List<UserDto> users = userService.findAll();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("UserInfo", filter); // @JsonFilter("UserInfo"), filter

        MappingJacksonValue mapping = new MappingJacksonValue(users);
        mapping.setFilters(filterProvider);
        
        return mapping;
    }

    @GetMapping("/admin/users/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable("id") Long id) {
        Optional<UserDto> user = userService.findOneUser(id);
        if (user.isPresent()) {
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("UserInfo", filter); // @JsonFilter("UserInfo"), filter

            MappingJacksonValue mapping = new MappingJacksonValue(user.get());
            mapping.setFilters(filterProvider);
            return mapping;
        }
        throw new UserNotFoundException(String.format("ID[%s] not found", id));
    }
}
