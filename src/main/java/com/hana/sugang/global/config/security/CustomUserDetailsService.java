package com.hana.sugang.global.config.security;

import com.hana.sugang.api.member.dto.response.MemberResponse;
import com.hana.sugang.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService
 *
 * 로그인 관련된 기능을 수행
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    /**
     *
     * @param username the username identifying the user whose data is required.
     * username으로 회원을 조회해서 UserDetails로 형변환 후 return
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        MemberResponse memberResponse = memberService.searchUser(username);
        return new CustomUserDetails(memberResponse);
    }
}
