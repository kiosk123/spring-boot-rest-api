# 스프링 부트로 하는 REST API 개발

# 순서
1. Spring Boot로 개발하는 RESTful Service  
2. User Service API 구현  
3. RESTful Service 기능 확장  
4. Spring Boot API 사용  
5. JPA 연동을 통한 REST API 개발  

# 유용한 코드
- 클래스의 인스턴스간 필드명이 같다면 같은 필드명에 대해서 데이터를 복사해서 집어넣는 메서드
  - `springframework`에서 제공한다.
```java
BeanUtils.copyProperties(source, target);
```