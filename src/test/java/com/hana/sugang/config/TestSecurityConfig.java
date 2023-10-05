package com.hana.sugang.config;

import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.dto.request.MemberCrate;
import com.hana.sugang.api.member.dto.response.MemberResponse;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.api.member.service.MemberService;
import com.hana.sugang.global.config.security.SecurityConfig;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * 스프링 시큐리티 설정을 추가하여 테스트 하기위한 클래스
 */
@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;


    @BeforeTestMethod
    public void securitySetUp() throws Exception {
        MemberCrate memberCreate = createMemberCreate();
        given(memberService.searchUser(anyString())).willReturn(MemberResponse.class.newInstance());

        given(memberService.saveMember(memberCreate))
                .willReturn(1L);
    }

    private Member createMember() {
        return Member.builder()
                .username("HANATest1")
                .password("123456")
                .name("hanana9506@gmail.com")
                .memberType(MemberType.STUDENT)
                .build();
    }

    private MemberCrate createMemberCreate() {
        return MemberCrate.builder()
                .username("HANATest2")
                .password("123456")
                .name("hanana9506@gmail.com")
                .memberType(MemberType.STUDENT)
                .build();
    }
}
