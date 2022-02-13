package com.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
