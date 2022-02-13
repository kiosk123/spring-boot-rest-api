package com.study.init;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

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
            
            users.add(createUser("user1", "test1", "701010-1111111"));
            users.add(createUser("user2", "test2", "801111-2222222"));
            users.add(createUser("user3", "test3", "901212-1111111"));

            users.forEach(userService::saveUser);
        }

        private User createUser(String name, String password, String ssn) {
            return User.builder()
            .name(name)
            .password(password)
            .ssn(ssn)
            .build();
        }
    }
}
