package com.hana.sugang.global.config.security;

import com.hana.sugang.api.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    @Getter
    private Member member;



    // form 로그인
    public CustomUserDetails(Member member) {
        this.member = member;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //권한부
        return null;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
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
