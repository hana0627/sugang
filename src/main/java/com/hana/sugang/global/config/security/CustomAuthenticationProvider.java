package com.hana.sugang.global.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // STEP1. authentication객체에서 username과 password를 가져온다.
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // STEP2. userDetailsService의 loadUserByUsername 메소르를 호출하여 사용자가 존재하는지 찾는다.
        CustomUserDetails u = userDetailsService.loadUserByUsername(username);

        // STEP3. password encoder를 이용해 사용자가 입력한 password와 db에 encode된 패스워드가 일치하는지 확인한다.
        if(passwordEncoder.matches(password,u.getPassword())) {
            // STEP4. 권한정보를 가져와서 담아준다.
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new CustomGrantedAuthority(u.getMemberResponse().memberType().getRoleName()));

            // 별도의 맞춤구성을 하지 않았으므로 UsernamePasswordAuthenticationToken형식으로 return 해준다.
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }

        throw new BadCredentialsException("Something was wrong");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
