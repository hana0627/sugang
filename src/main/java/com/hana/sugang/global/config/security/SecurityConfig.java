package com.hana.sugang.global.config.security;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티 설정 클래스
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(c -> c.disable()) //TODO
                .authorizeHttpRequests(auth -> auth
                        //css, img등 정적파일에 대한 접근 허용하는 설정
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/model/member/login").permitAll() // 로그인페이지는 전체접근가능
                        .anyRequest().permitAll())

                .formLogin(form -> form
                        .loginPage("/model/member/login"))

                .build();
    } 
}
