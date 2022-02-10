package com.study.user.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class UserRequestDto {
    private Long id;
    private String name;
    private LocalDateTime joinDate;
}
