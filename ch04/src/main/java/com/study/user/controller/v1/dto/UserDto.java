package com.study.user.controller.v1.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@JsonFilter("UserInfo")
@ApiModel(description = "사용자 상세 정보를 위한 객체") // Swagger
public class UserDto {
    private Long id;

    @ApiModelProperty(notes = "사용자 이름을 입력해 주세요") // Swagger
    private String name;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") /** JSON 날짜 응답 포맷 지정 */
    @ApiModelProperty(notes = "사용자 가입날짜") // Swagger
    private LocalDateTime joinDate;

    @ApiModelProperty(notes = "사용자 패스워드를 입력해주세요") // Swagger
    private String password;

    @ApiModelProperty(notes = "사용자 주민번호를 입력해주세요") // Swagger
    private String ssn;
}
