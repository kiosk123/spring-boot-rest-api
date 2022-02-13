package com.study.user.controller.v2;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.study.common.api.v2.V2Controller;
import com.study.common.exception.UserNotFoundException;
import com.study.user.controller.v1.dto.UserDto;
import com.study.user.controller.v1.dto.UserRequestDto;
import com.study.user.domain.User;
import com.study.user.service.UserService;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerV2 implements V2Controller {
    
    private final UserService userService;

    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers() {
        List<UserDto> users = userService.findAll();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("UserInfo", filter); // @JsonFilter("UserInfo"), filter

        MappingJacksonValue mapping = new MappingJacksonValue(users);
        mapping.setFilters(filterProvider);
        return mapping;
    }

    @GetMapping("/users/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable("id") Long id) {
        Optional<UserDto> user = userService.findOneUser(id);
        if (user.isPresent()) {
            // HATEOAS
            UserDto userDto = user.get();
            EntityModel<UserDto> model = EntityModel.of(userDto);

            // 현재 컨트롤러의 retrieveAllUser 메서드를 이용해서 링크 생성
            WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
            model.add(linkTo.withRel("all-users"));

            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "joinDate", "ssn");
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("UserInfo", filter); // @JsonFilter("UserInfo"), filter

            MappingJacksonValue mapping = new MappingJacksonValue(model);
            mapping.setFilters(filterProvider);
            
            return mapping;
        }

        throw new UserNotFoundException(String.format("ID[%s] not found", id));
    }

    /**
     * @Valid 어노테이션 추가하여 밸리데이션 활성화
     */
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserRequestDto userRequestDto) {

        User user = User.builder()
            .password(userRequestDto.getPassword())
            .name(userRequestDto.getName())
            .ssn(userRequestDto.getSsn())
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
