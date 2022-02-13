package com.study.common.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public abstract class BaseDto {
    protected int code = HttpStatus.OK.value();
    protected String message = "success";
}
