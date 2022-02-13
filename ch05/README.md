# JPA 연동을 통한 REST API 개발

## SecurityConfig 클래스 수정
- CSRF `disable()` 의 의미 - [링크](https://zzang9ha.tistory.com/341)
- frameOptions `disable()`의 의미 - [링크](https://gigas-blog.tistory.com/124)

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user")
            .password("123123")
            .roles("USER");
    }
    
    

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();   // csrf protection 기능을 disable한다
        http.headers().frameOptions().disable(); // Header의 FrameOption 기능을 disable 한다.

        http.authorizeRequests().antMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated();
        
        http.httpBasic();  // http basic 인증을 사용하겠다
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```

## JPA 엔티티 클래스 선언
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

    @Setter
    private String password;

    @Setter
    private String ssn;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinDate;

    @Builder
    private User(String name, String password, String ssn) {
        this.name = name;
        this.password = password;
        this.ssn = ssn;
    }
}
```
## `application.yml` 옵션 다음과 같이 설정
```yml
spring:
  messages:
    basename: messages # 다국어 식별 기본 파일명
  datasource:
    url: jdbc:h2:tcp://localhost/~/restapi
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
#        show_sql: true # System.out을 통해 출력
        format_sql: true
        use_sql_comments: true
        dialect: org.hibernate.dialect.H2Dialect
        default_batch_fetch_size: 100
```
## JPA Auditing을 사용하기 때문에 `@EnableJpaAuditing` 다음과 같이 설정
```java
@EnableJpaAuditing
@SpringBootApplication
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.KOREA);
		return localeResolver;
	}
}
```
## Spring Boot 실행시 SQL Scripts 파일 실행하는 방법
[링크](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.using-basic-sql-scripts)

## `UserRepository` 정의

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
}
```

## `UserService`정의
```java
RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Optional<UserDto> findOneUserDto(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) {
            User user = findUser.get();
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getJoinDate(), user.getPassword(), user.getSsn());
            return Optional.of(userDto);
        } else {
            return Optional.empty();
        }
    }

    public List<UserDto> findAll() {
        return userRepository
                    .findAll()
                    .stream()
                    .map(user -> new UserDto(user.getId(), user.getName(), user.getJoinDate(), user.getPassword(), user.getSsn()))
                    .collect(Collectors.toList());
    }

    @Transactional
    public void removeUser(Long id) {
        Optional<User> findUser = userRepository.findById(id);
        if (findUser.isPresent()) { 
            userRepository.delete(findUser.get());
        }
    }

    @Transactional
    public Optional<UserDto> updateUser(UserRequestDto userRequestDto) {
        Optional<User> findUser = userRepository.findById(userRequestDto.getId());
        if(findUser.isPresent()) {
            User user = findUser.get();
            user.setName(userRequestDto.getName());
            user.setPassword(userRequestDto.getPassword());
            user.setSsn(userRequestDto.getSsn());
            
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getJoinDate(), user.getPassword(), user.getSsn());
            return Optional.of(userDto);
        } 
        return Optional.empty();
    }
}
```

## Post 엔티티 추가 및 User 엔티티와 연관관계 설정
```java
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Post {
    @Id @GeneratedValue
    private Long id;

    @Setter
    String description;

    @Setter
    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    @Builder
    public Post(String description, User user) {
        this.description = description;
        this.user = user;
    }
}

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User {
    
    @Id @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @Setter
    private String password;

    @Setter
    private String ssn;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinDate;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    private User(String name, String password, String ssn, Post post) {
        this.name = name;
        this.password = password;
        this.ssn = ssn;
        posts.add(post);
        post.setUser(this);
    }

    /** Post 삭제시 사용될 수 있는 연관관계 편의 메서드 */
    public void removePost(Post post) {
        Iterator<Post> iter = posts.iterator();
        while (iter.hasNext()) {
            Post target = iter.next();
            if (target.getId().equals(post.getId())) {
                iter.remove();
                break;
            }
        }
    }
}
```

## PostRepository 생성
```java
public interface PostRepository extends JpaRepository<Post, Long>{

    @Query("select p from Post p join p.user on p.user.id = :userId")
    List<Post> getPostsByUser(@Param("userId") Long userId);
}
```

## PostService 작성
```java
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostDto> getPostsByUser(Long userId) {
        List<Post> posts = postRepository.getPostsByUser(userId);
        return posts.stream()
                .map(p -> new PostDto(p.getId(), p.getDescription(), p.getCreateDate(), p.getUpdateDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long savePostByUser(Long userId, PostDto postDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        User findUser = user.get();
        Post post = Post.builder().description(postDto.getDescription()).user(findUser).build();
        postRepository.save(post);
        return post.getId();
    }
}
```

## PostController 작성
```java

@RequiredArgsConstructor
@RestController
public class PostControllerV2 implements V2Controller {

    private final UserService userService;
    private final PostService postService;
    
    @GetMapping("/users/{userId}/posts")
    public List<PostDto> retrieveAllPostsByUser(@PathVariable("userId") Long userId) {
        Optional<UserDto> user = userService.findOneUser(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        
        return postService.getPostsByUser(userId); 
    }

    @PostMapping("/users/{userId}/posts")
    public ResponseEntity<Void> createPostByUser(@PathVariable("userId") Long userId, @RequestBody PostDto postDto) {
        Long postId = postService.savePostByUser(userId, postDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(postId).toUri();
        
        return ResponseEntity.created(location).build();
    }
}
```

## PUT과 DELETE는 차후에 구현 예정
