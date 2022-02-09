package com.study.controller;

import com.study.controller.dto.HelloDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello!";
    }
    
    @GetMapping(path = "/hello-dto")
    public HelloDto helloDto() {
        return new HelloDto("hello!");
    }

    @GetMapping(path = "/hello-dto/{name}")
    public HelloDto helloDtoUsingPathVariable(@PathVariable(name = "name") String name) {
        return new HelloDto(String.format("Hello! %s", name));
    }
}
