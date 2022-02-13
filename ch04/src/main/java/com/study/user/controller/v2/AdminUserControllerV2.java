package com.study.user.controller.v2;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.study.common.exception.UserNotFoundException;
import com.study.user.controller.v1.dto.UserDto;
import com.study.user.controller.v2.dto.UserDtoV2;
import com.study.user.service.UserService;

import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AdminUserControllerV2 {

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

    @GetMapping(value = "/admin/users/{id}", produces = "application/vnd.company.appv2+json") // vnd는 vender의 약자
    public MappingJacksonValue retrieveUser(@PathVariable("id") Long id) {
        Optional<UserDto> user = userService.findOneUser(id);
        if (user.isPresent()) {
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn", "grade");
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("UserInfoV2", filter); // @JsonFilter("UserInfoV2"), filter

            UserDtoV2 userDtoV2 = new UserDtoV2();
            userDtoV2.setGrade("VIP");

            BeanUtils.copyProperties(user.get(), userDtoV2); // 같은 필드명이 있다면 그 필드의 데이터를 동일한 필드명에 복사 - setter메서드가 필수적으로 있어야함

            MappingJacksonValue mapping = new MappingJacksonValue(userDtoV2);
            mapping.setFilters(filterProvider);
            return mapping;
        }
        throw new UserNotFoundException(String.format("ID[%s] not found", id));
    }
}
