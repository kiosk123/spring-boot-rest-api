# 1. Spring Boot로 개발하는 RESTful Service

## 프로젝트 구성

- java 11
- queryDSL 5.0
- spring-boot 2.6.3
- spring-boot-devtools
- spring-web
- h2 database
- thymeleaf
- lombok

**gradle 설정**
```gradle
buildscript { 
    ext { queryDslVersion = "5.0.0"
    } 
}

plugins {
	id 'org.springframework.boot' version '2.6.3'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id "com.ewerk.gradle.plugins.querydsl" version "1.0.10" //querydsl 추가 
	id 'java'
}

group = 'com.study'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	//querydsl 추가 
    implementation "com.querydsl:querydsl-jpa:${queryDslVersion}" 
    annotationProcessor "com.querydsl:querydsl-apt:${queryDslVersion}"

	compileOnly 'org.projectlombok:lombok'
	developmentOnly 'org.springframework.boot:spring-boot-devtools'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}

//querydsl 추가 시작 
def querydslDir = "$buildDir/generated/querydsl"
querydsl { 
    jpa = true 
	querydslSourcesDir = querydslDir 
}

sourceSets {
     main.java.srcDir querydslDir 
} 

configurations { 
    querydsl.extendsFrom compileClasspath 
}

compileQuerydsl { 
     options.annotationProcessorPath = configurations.querydsl 
} 
//querydsl 추가 끝
```

**application.yml 설정**
```yml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/restapi
    username: sa
    password: 
    driver-class-name: org.h2.Driver
    
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
#        show_sql: true # System.out을 통해 출력
        format_sql: true
        use_sql_comments: true
        dialect: org.hibernate.dialect.H2Dialect
        default_batch_fetch_size: 100

logging:
  level:
    org.hibernate.SQL: debug #logger를 통해 출력
    org.hibernate.type: trace #SQL 쿼리 파라미터를 확인할 수 있다
```

# REST API 설계
|설명|REST API|HTTP Method|
|---|---|---|
|모든 사용자 조회|/users|GET|
|사용자 생성|/users|POST|
|특정 사용자 조회|/users/{id}|GET|
|특정 사용자 삭제|/users/{id}|DELETE|
|특정 사용자의 모든 포스트 조회|/users/{id}/posts|GET|
|특정 사용자의 포스트 생성|/users/{id}/posts|POST|
|특정 사용자의 특정 포스트의 내용 조회|/users/{id}/posts/{post_id}|GET|