package com.study.user.controller.v2;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.study.common.api.v2.V2Controller;
import com.study.common.exception.UserNotFoundException;
import com.study.user.controller.v1.dto.UserDto;
import com.study.user.controller.v2.dto.UserDtoV2;
import com.study.user.service.UserService;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AdminUserControllerV2 implements V2Controller {

    private final UserService userService;

    @GetMapping("/admin/users")
    public MappingJacksonValue retrieveAllUsers() {
        List<UserDto> users = userService.findAll();

        // 컬렉션을 반환할때는 CollectionModel을 사용
        CollectionModel<UserDto> model = CollectionModel.of(users);

        // 현재 컨트롤러의 retrieveAllUser 메서드를 이용해서 링크 생성
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("UserInfo", filter); // @JsonFilter("UserInfo"), filter

        MappingJacksonValue mapping = new MappingJacksonValue(users);
        mapping.setFilters(filterProvider);
        
        return mapping;
    }

    @GetMapping(value = "/admin/users/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable("id") Long id) {
        Optional<UserDto> user = userService.findOneUser(id);
        if (user.isPresent()) {
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn", "grade");
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("UserInfoV2", filter); // @JsonFilter("UserInfoV2"), filter

            UserDtoV2 userDtoV2 = new UserDtoV2();
            userDtoV2.setGrade("VIP");

            BeanUtils.copyProperties(user.get(), userDtoV2); // 같은 필드명이 있다면 그 필드의 데이터를 동일한 필드명에 복사 - setter메서드가 필수적으로 있어야함
            
            EntityModel<UserDtoV2> model = EntityModel.of(userDtoV2);

            // 현재 컨트롤러의 retrieveUser 메서드를 이용해서 링크 생성
            WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveUser(userDtoV2.getId()));
            model.add(linkTo.withRel("user-url"));

            MappingJacksonValue mapping = new MappingJacksonValue(model);
            mapping.setFilters(filterProvider);
            return mapping;
        }
        throw new UserNotFoundException(String.format("ID[%s] not found", id));
    }
}
