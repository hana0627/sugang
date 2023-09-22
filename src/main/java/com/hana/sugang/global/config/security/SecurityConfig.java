package com.hana.sugang.global.config.security;


import com.hana.sugang.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티 설정 클래스
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final MemberService memberService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //TODO 오랫만에 해보는거라 일단 뼈대만 잡았음
        // 리팩토링
        return http
                .csrf(c -> c.disable()) //TODO
                .authorizeHttpRequests(auth -> auth
                        //css, img등 정적파일에 대한 접근 허용하는 설정
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .anyRequest().permitAll())

                .formLogin(form -> form
                        .loginPage("/model/member/login"))
                .build();
    } 
}
