package com.hana.sugang.api.member.service;

import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.dto.response.MemberResponse;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.exception.MemberNotFoundException;
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
    public MemberResponse searchUser(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);

        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .name(member.getName())
                .memberType(member.getMemberType())
                .currentScore(member.getCurrentScore())
                .maxScore(member.getMaxScore())
                .build();

    }

}
