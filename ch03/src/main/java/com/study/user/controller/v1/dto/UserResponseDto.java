package com.study.user.controller.v1.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.common.dto.BaseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserResponseDto extends BaseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserDto> users;

    public UserResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
