package com.hana.sugang.global.config.security;


import com.hana.sugang.api.member.domain.constant.MemberType;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티 설정 클래스
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    String[] professors = {
        MemberType.PROFESSOR.getRoleName(),
        MemberType.MANAGER.getRoleName(),
        MemberType.ADMIN.getRoleName(),
    };
    String[] students = {
        MemberType.STUDENT.getRoleName(),
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(c -> c.disable()) //TODO
                .authorizeHttpRequests(auth -> auth
                        //css, img등 정적파일에 대한 접근 허용하는 설정
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/model/member/login").permitAll() // 로그인페이지는 전체접근가능
//                        .requestMatchers(HttpMethod.POST,"/course/**").hasAnyRole(professors) // 강의도메인 POST 요청은 교수, 관리자급만 접근 가능
//                        .requestMatchers(HttpMethod.POST,"/course/apply").hasAnyRole(students) // 수강신청에 대한 호출은 학생만 접근 가능
                        .anyRequest().permitAll())

                .formLogin(form -> form
                        .loginPage("/model/member/login"))

                .build();
    } 
}
