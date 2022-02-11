# 3. RESTful Service 기능 확장  

## RESTful 서비스를 위한 유효성 체크 구현 - 1
- 의존성 추가
  - 밸리데이션을 사용하기 위해서는 다음의 의존성을 추가한다
  - `implementation 'org.springframework.boot:spring-boot-starter-validation'`

- 유효성 검증 추가
  - DTO  
  ```java
  @NoArgsConstructor
  @Getter @Setter
  public class UserRequestDto {
      private Long id;
      
      @Size(min = 2)
      private String name;
  
      @JsonSerialize(using = LocalDateTimeSerializer.class)
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") /** JSON 날짜 응답 포맷 지정 */
      @Past
      private LocalDateTime joinDate;
  }
  ```
  - Controller  
  ```java
  @RestController
  @RequiredArgsConstructor
  public class UserControllerV2 implements V2Controller {
      
      private final UserService userService;
  
      @GetMapping("/users")
      public List<UserDto> retrieveAllUsers() {
          List<UserDto> users = userService.findAll();
          return users;
      }
  
      @GetMapping("/users/{id}")
      public UserDto retrieveUser(@PathVariable("id") Long id) {
          Optional<UserDto> user = userService.findOneUser(id);
          if (user.isPresent()) {
             return user.get();
          }
          throw new UserNotFoundException(String.format("ID[%s] not found", id));
      }
  
      /**
       * @Valid 어노테이션 추가하여 밸리데이션 활성화
       */
      @PostMapping("/users")
      public ResponseEntity<Void> createUser(@RequestBody @Valid UserRequestDto userRequestDto) {
          User user = User.builder()
              .name(userRequestDto.getName())
              .build();
          Long id = userService.saveUser(user);
  
          URI location = ServletUriComponentsBuilder.fromCurrentRequest()
              .path("/{id}") 
              .buildAndExpand(id)
              .toUri();
          return ResponseEntity.created(location).build();
      }
  
      @DeleteMapping("/users/{id}")
      public void removeUser(@PathVariable("id") Long id) {
          userService.removeUser(id);
      }
  
      /**
       * @Valid 어노테이션 추가하여 밸리데이션 활성화
       */
      @PutMapping("/users")
      public ResponseEntity<Void> updateUser(@RequestBody @Valid UserRequestDto userRequestDto) {
          Optional<UserDto> findUser = userService.updateUser(userRequestDto);
          if (findUser.isEmpty()) {
              throw new UserNotFoundException(String.format("ID[%s] not found", userRequestDto.getId()));
          }
          URI location = ServletUriComponentsBuilder.fromCurrentRequest()
              .path("/{id}")
              .buildAndExpand(userRequestDto.getId())
              .toUri();
          
          HttpHeaders responseHeaders = new HttpHeaders();
          responseHeaders.setLocation(location);
          
          return new ResponseEntity<Void>(responseHeaders, HttpStatus.OK);
      }
  }
  ```
  - 예외 핸들러 메서드 추가
  ```java
  @RestController
  @ControllerAdvice /** 모든 컨트롤러가 실행될때 @ControllerAdvice가 붙은 빈이 실행되도록 되어있다 */
  public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
      //... 생략
      /**
       * 밸리데이션 실패에 대한 응답을 처리하는 메서드 오버라이드 하여 구현
       */
      @Override
      public final ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
  
          ExceptionResponse exceptionResponse 
              = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), ex.getBindingResult().toString());
          return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
      }
  }
  ```
  - 결과  
  ![.](./img/1.png)  

## RESTful 서비스를 위한 유효성 체크 구현 - 2
- 유효성 검증 DTO에 검증실패시 메시지 추가
```java
@NoArgsConstructor
@Getter @Setter
public class UserRequestDto {
    private Long id;
    
    @Size(min = 2, message = "name은 2글자 입력해주세요")
    private String name;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") /** JSON 날짜 응답 포맷 지정 */
    @Past(message = "날짜 포맷이 \'yyyy-MM-dd HH:mm:ss\'이 아닙니다")
    private LocalDateTime joinDate;
}
```
- 예외 핸들러 메서드 수정
```java
@RestController
@ControllerAdvice /** 모든 컨트롤러가 실행될때 @ControllerAdvice가 붙은 빈이 실행되도록 되어있다 */
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    //...
    /**
     * 밸리데이션 실패에 대한 응답을 처리하는 메서드 오버라이드 하여 구현
     */
    @Override
    public final ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
            
            String defaultMessage = ex.getAllErrors().get(0).getDefaultMessage();

        ExceptionResponse exceptionResponse 
            = new ExceptionResponse(LocalDateTime.now(), defaultMessage, ex.getBindingResult().toString());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
```
- 결과  
![.](./img/2.png)  

## 다국어 처리
