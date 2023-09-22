package com.hana.sugang.global.config.security;

import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    //TODO 예외변경
    /**
     *
     * @param username the username identifying the user whose data is required.
     * username으로 회원을 조회해서 UserDetails로 형변환 후 return
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        //TODO 리팩토링
        return memberService.searchUser(username)
                .map(CustomUserDetails::new)
                .orElseThrow(EntityNotFoundException::new);

    }
}
