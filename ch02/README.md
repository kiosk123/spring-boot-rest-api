# 2. User Service API 구현
## UserRest API 설계
## REST API 설계

|설명|REST API|HTTP Method|
|---|---|---|
|모든 사용자 조회|/users|GET|
|사용자 생성|/users|POST|
|특정 사용자 조회|/users/{id}|GET|
|특정 사용자 삭제|/users/{id}|DELETE|

## User Domain 생성
```java
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User {
    
    @Id @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinDate;

    @Builder
    private User(String name) {
        this.name = name;
    }
}
```

## UserRepository 생성

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
}
```

## UserService 생성
```java
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Optional<UserDto> findOneUser(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getJoinDate());
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }

    public List<UserDto> findAll() {
        return userRepository
                    .findAll()
                    .stream()
                    .map(user -> new UserDto(user.getId(), user.getName(), user.getJoinDate()))
                    .collect(Collectors.toList());
    }

    @Transactional
    public void removeUser(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) { 
            userRepository.delete(findUser.get());
        }
    }
}
```

## 애플리케이션 시작할 때 User 데이터를 집어 넣기 위한 DataInitializer 작성
```java
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Optional<UserDto> findOneUser(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getJoinDate());
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }
}
```

## DTO 작성
```java
@Setter @Getter
public abstract class BaseDto {
    protected int code = HttpStatus.OK.value();
    protected String message = "success";
}

@AllArgsConstructor
@Getter
public class UserDto {
    private Long id;

    @Setter
    private String name;

    @Setter
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") /** JSON 날짜 응답 포맷 지정 */
    private LocalDateTime joinDate;
}

@Getter @Setter
@NoArgsConstructor
public class UserResponseDto extends BaseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;        // 필드가 NULL 일시 JSON 프로퍼티에서 삭제됨

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserDto> users; // 필드가 NULL 일시 JSON 프로퍼티에서 삭제됨

    public UserResponseDto(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
```
