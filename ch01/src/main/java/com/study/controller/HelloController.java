package com.study.controller;

import com.study.controller.dto.HelloDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello!";
    }
    
    @GetMapping("/hello-dto")
    public HelloDto helloDto() {
        return new HelloDto("hello!");
    }
}
