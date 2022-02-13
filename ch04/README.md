# 4. Spring Boot API 사용  
## REST API에 HATEOAS 적용
![.](./img/1.png)  

- `build.gradle`에 의존성 추가

```gradle
implementation 'org.springframework.boot:spring-boot-starter-hateoas'
```

- `spring boot 2.1.x`와 `spring boot 2.2.x`이상 버전에서 HATEOAS 구현시 차이점
![.](./img/2.png)  

- Spring Boot 2.6.3 버전 기준으로 HAEOAS 구현
```java
@GetMapping("/users/{id}")
public EntityModel<UserDto> retrieveUser(@PathVariable("id") Long id) {
    Optional<UserDto> user = userService.findOneUser(id);
    if (user.isPresent()) {
        // HATEOAS
        UserDto userDto = user.get();
        EntityModel<UserDto> model = EntityModel.of(userDto);

        // 현재 컨트롤러의 retrieveAllUser 메서드를 이용해서 링크 생성
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));
        return model;
    }

    throw new UserNotFoundException(String.format("ID[%s] not found", id));
}
```

- 결과  
![.](./img/3.png)  

## REST API Documentation을 위한 Swagger 사용 - [오류발생시 참고](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)
![.](./img/4.png)  

- build.gradle에 Swagger 의존성 추가
```gradle
implementation 'io.springfox:springfox-boot-starter:3.0.0' // Swagger
implementation 'org.springframework.boot:spring-boot-starter-parent:2.4.0' //Swagger
```

- Swagger 구성 클래스 작성

