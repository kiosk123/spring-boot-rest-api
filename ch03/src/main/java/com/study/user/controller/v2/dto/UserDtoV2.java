package com.study.user.controller.v2.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.study.user.controller.v1.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@JsonFilter("UserInfoV2")
public class UserDtoV2 extends UserDto {
    private String grade;
}
