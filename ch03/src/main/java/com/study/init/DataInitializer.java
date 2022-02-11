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
            
            users.add(createUser("Kenneth"));
            users.add(createUser("Alice"));
            users.add(createUser("Elena"));

            users.forEach(userService::saveUser);
        }

        private User createUser(String name) {
            return User.builder()
            .name(name)
            .build();
        }
    }
}
