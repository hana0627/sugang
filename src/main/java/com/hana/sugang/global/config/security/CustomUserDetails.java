package com.hana.sugang.global.config.security;

import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.dto.response.MemberResponse;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 로그인시 스프링시큐리티에서 이용할
 * 회원정보를 담는 객체
 */
public class CustomUserDetails implements UserDetails {

    @Getter
    private MemberResponse memberResponse;

    // form 로그인
    public CustomUserDetails(MemberResponse memberResponse) {
        this.memberResponse = memberResponse;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //권한부
        return null;
    }

    @Override
    public String getPassword() {
        return memberResponse.password();
    }

    @Override
    public String getUsername() {
        return memberResponse.username();
    }







    /**
     * == 만료되었는가, 잠겼는가, 기한만료인가, 사용중인가  등에 관련된 설정 ==
     */

    @Override
    public boolean isAccountNonExpired() {return true; }

    @Override
    public boolean isAccountNonLocked() {return true; }

    @Override
    public boolean isCredentialsNonExpired() {return true; }

    @Override
    public boolean isEnabled() {return true; }
}
