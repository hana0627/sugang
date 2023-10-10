package com.hana.sugang.doc.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.dto.request.MemberCrate;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.config.security.CustomPasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * CourseController - 문서화작업을 위한 테스트 클래스
 * 테스트 성공시 asciidoc에 추가됨
 */
@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.hanaSugang.com", uriPort = 443)
@DisplayName("[RESTDocs] MemberControllerDocTest")
public class MemberControllerDocTest {
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
        for (int i = 0; i < 20; i++) {
            Member member = createMember();
            memberRepository.save(member);

        }
        //when & then
        mvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print())
                .andDo(document("memberList"));
    }

    @Test
    @DisplayName("회원 한명 조회")
    void member() throws Exception {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        //when & then
        mvc.perform(RestDocumentationRequestBuilders.get("/member/{userName}", savedMember.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(savedMember.getUsername()))
                .andExpect(jsonPath("$.name").value(savedMember.getName()))
                .andExpect(jsonPath("$.memberType").value("STUDENT"))
                .andDo(print())
                .andDo(document("findMember",
                        pathParameters(
                                parameterWithName("userName").description("회원코드")
                        ),
                        responseFields(
                                fieldWithPath("id").description("회원Id"),
                                fieldWithPath("username").description("회원코드"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("회원이름"),
                                fieldWithPath("memberType").description("회원구분"),
                                fieldWithPath("currentScore").description("현재신청학점"),
                                fieldWithPath("maxScore").description("최대신청가능학점")
                        )
                ));
    }

    @Test
    @DisplayName("회원 저장")
    void saveMember() throws Exception {
        //given
        MemberCrate memberCreate = createMemberCreate();
        String json = objectMapper.writeValueAsString(memberCreate);


        //when & then
        mvc.perform(RestDocumentationRequestBuilders.post("/member")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("createMember",
                        requestFields(
                                fieldWithPath("username").description("회원코드"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("회원이름"),
                                fieldWithPath("memberType").description("회원구분").attributes(key("constraint").value("교수 : PROFESSOR / 학생 : STUDENT / 교직원 : STAFF"))
                        ),
                        responseFields(
                                fieldWithPath("id").description("회원Id")
                        )
                ));
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

    private MemberCrate createMemberCreateNoMemberType() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return MemberCrate.builder()
                .username(uuid + "test")
                .password("123456")
                .name(uuid)
                .build();
    }

    private MemberCrate createMemberCreateNoUsername() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return MemberCrate.builder()
                .password("123456")
                .name(uuid)
                .memberType(MemberType.STUDENT)
                .build();
    }

    private MemberCrate createMemberCreateNoNameAndNoPassword() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return MemberCrate.builder()
                .username(uuid + "test")
                .memberType(MemberType.STUDENT)
                .build();
    }

}
