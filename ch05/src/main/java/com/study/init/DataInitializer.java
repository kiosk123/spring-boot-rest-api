package com.study.init;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.study.post.domain.Post;
import com.study.user.domain.User;
import com.study.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class DataInitializer {

    @Autowired
    private DataInitHandler handler;

    @PostConstruct
    public void init() {
        handler.insertInitData();
    }
    
    @Component
    @Transactional
    static class DataInitHandler {

        @Autowired
        private UserService userService;

        public void insertInitData() {
            List<User> users = new ArrayList<>();
            
            users.add(createUser("user1", "test1", "701010-1111111", "post1"));
            users.add(createUser("user2", "test2", "801111-2222222", "post2"));
            users.add(createUser("user3", "test3", "901212-1111111", "post3"));

            users.forEach(userService::saveUser);
        }

        private User createUser(String name, String password, String ssn, String description) {
            return User.builder()
            .name(name)
            .password(password)
            .ssn(ssn)
            .post(Post.builder().description(description).build())
            .build();
        }
    }
}
