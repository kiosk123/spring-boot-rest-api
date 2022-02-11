package com.study.user.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class UserRequestDto {
    private Long id;
    
    @Size(min = 2) /** name 필드는 최소 2글자 이상은 되어야함 */
    private String name;

    @Past
    private LocalDateTime joinDate;
}
