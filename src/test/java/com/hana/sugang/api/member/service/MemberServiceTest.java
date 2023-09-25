package com.hana.sugang.api.member.service;

import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.dto.request.MemberCrate;
import com.hana.sugang.api.member.dto.response.MemberResponse;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.config.security.CustomPasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * 훈련용 예제인만큼
 * Mockkito를 이용한
 * 단위테스트로 진행
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
class MemberServiceTest {

    @InjectMocks
    private MemberService sut; //System Under Test 테스트 대상임을 표기

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CustomPasswordEncoder passwordEncoder;
    @Test
    @DisplayName("회원 전체 조회")
    void findMembers() {
        //given
        given(memberRepository.findAll()).willReturn(List.of());


        //when
        List<MemberResponse> members = sut.findMembers();

        //then
        then(memberRepository).should().findAll();

    }

    @Test
    @DisplayName("회원 한명 조회")
    void searchUser() {
        //given
        Member member = createMember();
        String username = member.getUsername();
        given(memberRepository.findByUsername(username)).willReturn(Optional.of(member));


        //when
        MemberResponse result = sut.searchUser(username);

        //then
        assertThat(result)
                .hasFieldOrPropertyWithValue("username", member.getUsername())
                .hasFieldOrPropertyWithValue("name", member.getName())
                .hasFieldOrPropertyWithValue("password", member.getPassword())
                .hasFieldOrPropertyWithValue("memberType", member.getMemberType());


        then(memberRepository).should().findByUsername(username);

    }


    @Test
    @DisplayName("회원 등록")
    void saveMember() {
        //given
        MemberCrate memberCreate = createMemberCreate();
        Member member = Member.builder()
                .username(memberCreate.username())
                .password(memberCreate.password())
                .name(memberCreate.name())
                .memberType(memberCreate.memberType())
                .build();

        given(memberRepository.save(any(Member.class))).willReturn(member);


        //when
        sut.saveMember(memberCreate);


        //then
        then(memberRepository).should().save(any(Member.class));

    }


    private Member createMember() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return Member.builder()
                .username(uuid + "test")
                .password("123456")
                .name(uuid)
                .memberType(MemberType.STUDENT)
                .build();
    }


    private MemberCrate createMemberCreate() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return MemberCrate.builder()
                .username(uuid + "test")
                .password("123456")
                .name(uuid)
                .memberType(MemberType.STUDENT)
                .build();
    }


}