package com.hana.sugang.api.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.dto.request.MemberCrate;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.config.TestSecurityConfig;
import com.hana.sugang.global.config.security.CustomPasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Member Controller 테스트")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 전체조회")
    void memberList() throws Exception {
        //given
//        for(int i = 0; i<20; i++) {
//            Member member = createMember();
//            memberRepository.save(member);
//
//        }
        //when & then
        mvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print());

    }


    @Test
    @DisplayName("회원 한명 조회")
    void member() throws Exception {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        //when & then
        mvc.perform(get("/member/{userName}", savedMember.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(savedMember.getUsername()))
                .andExpect(jsonPath("$.name").value(savedMember.getName()))
                .andExpect(jsonPath("$.memberType").value("STUDENT"))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 저장")
    void saveMember() throws Exception {
        //given
        long before = memberRepository.count();
        MemberCrate memberCreate = createMemberCreate();
        String json = objectMapper.writeValueAsString(memberCreate);


        //when
        mvc.perform(post("/member")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        long after = memberRepository.count();

        assertThat(after).isEqualTo(before+1);

        Member member = memberRepository.findAll().get((int) (after - 1));
        assertThat(member.getUsername()).isEqualTo(memberCreate.username());
        assertThat(member.getName()).isEqualTo(memberCreate.name());
        assertThat(member.getMemberType()).isEqualTo(memberCreate.memberType());

        assertThat(true).isEqualTo(passwordEncoder.matches(memberCreate.password(), member.getPassword()));
    }

    @Test
    @DisplayName("회원저장시 username이 중복되면 예외를 발생시킨다.")
    void saveMemberWithSameUsername() throws Exception {
        //given
        MemberCrate memberCreate = createMemberCreate();
        Member member = Member
                .builder()
                .username(memberCreate.username())
                .password(memberCreate.password())
                .name(memberCreate.name())
                .memberType(memberCreate.memberType())
                .build();
        memberRepository.save(member);

        String json = objectMapper.writeValueAsString(memberCreate);


        //when & then
        mvc.perform(post("/member")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("중복된 회원코드 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 저장시 회원타입은 필수값이다.")
    void saveMemberWithNoMemberType() throws Exception {
        //given
        MemberCrate memberCreate = createMemberCreateNoMemberType();
        String json = objectMapper.writeValueAsString(memberCreate);


        //when & then
        mvc.perform(post("/member")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.memberType").value("회원 구분을 선택해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 저장시 username값은 필수값이다.")
    void saveMemberWithNoUsername() throws Exception {
        //given
        MemberCrate memberCreate = createMemberCreateNoUsername();
        String json = objectMapper.writeValueAsString(memberCreate);


        //when & then
        mvc.perform(post("/member")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.username").value("회원코드를 입력해주세요."))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 저장시 이름과 패스워드는 필수값이다.")
    void saveMemberWithNoNameAndNoPassword() throws Exception {
        //given
        MemberCrate memberCreate = createMemberCreateNoNameAndNoPassword();
        String json = objectMapper.writeValueAsString(memberCreate);


        //when & then
        mvc.perform(post("/member")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요."))
                .andExpect(jsonPath("$.validation.name").value("이름을 입력해주세요."))
                .andDo(print());
    }


    private Member createMember() {
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return Member.builder()
                .username(uuid + "test")
                .password("123456")
                .name(uuid)
                .memberType(MemberType.STUDENT)
                .build();
    }


    private MemberCrate createMemberCreate() {
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return MemberCrate.builder()
                .username(uuid + "test")
                .password("123456")
                .name(uuid)
                .memberType(MemberType.STUDENT)
                .build();
    }

    private MemberCrate createMemberCreateNoMemberType() {
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return MemberCrate.builder()
                .username(uuid + "test")
                .password("123456")
                .name(uuid)
                .build();
    }

    private MemberCrate createMemberCreateNoUsername() {
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return MemberCrate.builder()
                .password("123456")
                .name(uuid)
                .memberType(MemberType.STUDENT)
                .build();
    }
    private MemberCrate createMemberCreateNoNameAndNoPassword() {
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return MemberCrate.builder()
                .username(uuid + "test")
                .memberType(MemberType.STUDENT)
                .build();
    }
}