package com.study.hello.controller.v1;

import java.util.Locale;

import com.study.common.api.v1.V1Controller;

import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HelloControllerV1 implements V1Controller {
    
    private final MessageSource messageSource;

    @GetMapping(path = "/hello-world-internationalized")
    public String helloWorldInternationalized( // 요청 헤더의 Accept-Language의 값으로 Locale 설정하고 없으면 부트에서 설정한 기본 Locale 적용
            @RequestHeader(name = "Accept-Language", required = false)  Locale locale) {
        return messageSource.getMessage("greeting.message", null, locale);
    }

    // @GetMapping(path = "/current-locale")
    // public LocaleDto currentLocale() {
    //     return new LocaleDto(LocaleContextHolder.getLocale());
    // }

    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Setter @Getter
    // static class LocaleDto {
    //     private Locale currentLocale;

    // }
}
