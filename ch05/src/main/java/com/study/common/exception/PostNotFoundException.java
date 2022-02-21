package com.study.common.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}
