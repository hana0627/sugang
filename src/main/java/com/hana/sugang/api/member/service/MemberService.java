package com.hana.sugang.api.member.service;

import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.dto.request.MemberCrate;
import com.hana.sugang.api.member.dto.response.MemberResponse;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.config.security.CustomPasswordEncoder;
import com.hana.sugang.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final CustomPasswordEncoder customPasswordEncoder;


    /**
     * 
     * @param username
     * 회원 한명 조회
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

    /**
     * 회원전체조회
     * 조회한 회원 List<Entity>를 List<Dto>로 변환하여 return
     */
    public List<MemberResponse> findMembers() {
        List<Member> members = memberRepository.findAll();
        List<MemberResponse> result = new ArrayList<>();
        
        
        for (Member member : members) {
            result.add(
                    MemberResponse.builder()
                            .id(member.getId())
                            .username(member.getUsername())
                            .password(member.getPassword())
                            .name(member.getName())
                            .memberType(member.getMemberType())
                            .currentScore(member.getCurrentScore())
                            .maxScore(member.getMaxScore())
                            .build()
            );
        }

        return result;
    }


    /**
     * 회원저장
     */
    @Transactional
    public Long saveMember(MemberCrate requestDto) {
        Member member = Member.builder()
                .username(requestDto.username())
                .password(customPasswordEncoder.encode(requestDto.password()))
                .name(requestDto.name())
                .memberType(requestDto.memberType())
                .build();
        
        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }
}
