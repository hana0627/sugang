package com.hana.sugang.api.member.service;

import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;


    /**
     * dto가 아니라 Entity를 그대로 내려주는 이유
     * 컨트롤러에서 바로 호출해서 사용하는것이 아니라,
     * 시큐리티 UserDetailsService에서 후처리 수행
     */
    public Optional<Member> searchUser(String username) {
        return memberRepository.findByUsername(username);
    }

}
