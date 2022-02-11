# 3. RESTful Service 기능 확장  

## RESTful 서비스를 위한 유효성 체크 구현 - 1
- 의존성 추가
  - 밸리데이션을 사용하기 위해서는 다음의 의존성을 추가한다
  - `implementation 'org.springframework.boot:spring-boot-starter-validation'`

- 유효성 검증 추가
```java
@NoArgsConstructor
@Getter @Setter
public class UserRequestDto {
    private Long id;
    
    @Size(min = 2) /** name 필드는 최소 2글자 이상은 되어야함 */
    private String name;

    @Past /** 날짜는 미래 날짜를 쓸수 없고 현재날짜 포함 과거날짜만 쓸 수 있음*/
    private LocalDateTime joinDate;
}
```