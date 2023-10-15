package com.hana.sugang.doc.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.redis.CourseRedisRepository;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.repository.MemberRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CourseController - 문서화작업을 위한 테스트 클래스
 * 테스트 성공시 asciidoc에 추가됨
 */
@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.hanaSugang.com", uriPort = 443)
@DisplayName("[RESTDocs] CourseControllerDocTest")
public class CourseControllerDocTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseRedisRepository courseRedisRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void before() {
        memberRepository.deleteAll();
        courseRepository.deleteAll();
        courseRedisRepository.flushAll();
        //RESTDoc에 알맞는 설정을 MockMvc에 추가
//    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
//        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .apply(documentationConfiguration(restDocumentation))
//                .build();
    }

    @AfterEach
    void after() {
        memberRepository.deleteAll();
        courseRepository.deleteAll();
        courseRedisRepository.flushAll();
    }


    @Test
    @DisplayName("restDoc 설정테스트")
    void DocTest() throws Exception{
        //given
        //nothing

        // when&then
        this.mvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                // 문서에는 isOk()였으나 rootPath를 따로 만들지 않았으므로 is4xxClientError로 변경
                .andExpect(status().is4xxClientError())
                .andDo(document("index"));
    }

    @Test
    @DisplayName("강의전체조회")
    void courseList() throws Exception{
        //given
        for(int i = 1; i <=50; i++) {
            if(i <10 && i %2 == 0) {
                CourseCreate requestDto = CourseCreate.of("ABCD0"+String.valueOf(i),"강의명"+i ,"설명입니다.",30, CourseType.CC,3 );
                courseRepository.save(requestDto.toEntity(requestDto));
            }
            else if(i <10) {
                CourseCreate requestDto = CourseCreate.of("ABCD0"+String.valueOf(i),"강의명"+i ,"설명입니다.",30, CourseType.GC,3 );
                courseRepository.save(requestDto.toEntity(requestDto));
            }
            else {
                CourseCreate requestDto = CourseCreate.of("ABCD"+String.valueOf(i),"강의명"+i ,"설명입니다.",30, CourseType.CC,3 );
                courseRepository.save(requestDto.toEntity(requestDto));
            }
        }

        // when&then
        this.mvc.perform(get("/courses").accept(MediaType.APPLICATION_JSON))
                // 문서에는 isOk()였으나 rootPath를 따로 만들지 않았으므로 is4xxClientError로 변경
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",  Matchers.is(25)))
                .andExpect(jsonPath("$.[0].title").value("강의명50"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print())
                .andDo(document("courseList"));
    }


    @Test
    @DisplayName("강의 단건 조회")
    void findCourse() throws Exception {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedEntity = courseRepository.save(CourseCreate.toEntity(requestDto));

        //when & then
        mvc.perform(RestDocumentationRequestBuilders.get("/course/{id}", savedEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("ZZZZ01"))
                .andExpect(jsonPath("$.title").value("테스트등록강의"))
                .andExpect(jsonPath("$.description").value("설명입니다."))
                .andExpect(jsonPath("$.courseType").value("CC"))
                .andExpect(jsonPath("$.score").value("3"))
                .andExpect(jsonPath("$.maxCount").value("30"))
                .andDo(print())
                //RESTDocs 필드 추가
                .andDo(document("findCourse",
                        pathParameters(
                                parameterWithName("id").description("강의 Id")
                        ),
                        responseFields(
                                //모든 jsonBody내용을 입력해야 테스트가 통과됨
                                fieldWithPath("id").description("강의Id"),
                                fieldWithPath("code").description("강의코드"),
                                fieldWithPath("title").description("강의명"),
                                fieldWithPath("description").description("강의설명"),
                                fieldWithPath("courseType").description("교양/전공타입"),
                                fieldWithPath("score").description("학점"),
                                fieldWithPath("currentCount").description("현재수강인원"),
                                fieldWithPath("maxCount").description("최대수강인원")
                        )
                ));
    }

    @Test
    @DisplayName("강의 등록")
    void saveCourse() throws Exception {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );

        String json = objectMapper.writeValueAsString(requestDto);


        //when
        mvc.perform(RestDocumentationRequestBuilders.post("/course")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(document("createCourse",
                        requestFields(
                                fieldWithPath("code").description("강의코드"),
                                fieldWithPath("title").description("강의명"),
                                fieldWithPath("description").description("강의설명").optional(),
                                fieldWithPath("maxCount").description("최대수강인원수"),
                                fieldWithPath("courseType").description("교양/전공여부").attributes(key("constraint").value("전공 : CC / 교양 : GC")),
                                fieldWithPath("score").description("강의학점")
                        ),
                        responseFields(
                                fieldWithPath("id").description("강의Id")
                        )
                ));


        //then
    }


    @Test
    @DisplayName("강의삭제")
    @Transactional
    void deleteCourse() throws Exception {
        //given
        CourseCreate createCourse = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(createCourse));

        //when & then
        mvc.perform(RestDocumentationRequestBuilders.delete("/course/{id}", savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print())
                .andDo(document("deleteCourse",
                        pathParameters(
                                parameterWithName("id").description("강의 Id")
                        ),
                        responseFields(
                                //모든 jsonBody내용을 입력해야 테스트가 통과됨
                                fieldWithPath("id").description("강의Id")
                        )
                ));

    }

    @Test
    @DisplayName("강의정보 수정")
    void editCourse() throws Exception {
        //given
        CourseCreate createCourse = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(createCourse));

        CourseEdit editCourse = CourseEdit.of("ZZZZ02", "테스트강의수정", 33, CourseType.GC, 2);
        String json = objectMapper.writeValueAsString(editCourse);

        //when & then
        mvc.perform(RestDocumentationRequestBuilders.patch("/course/{id}", savedCourse.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("editCourse",
                        pathParameters(
                                parameterWithName("id").description("강의 Id")
                        ),
                        requestFields(
                                fieldWithPath("title").description("강의명"),
                                fieldWithPath("description").description("강의설명").optional(),
                                fieldWithPath("maxCount").description("최대수강인원수"),
                                fieldWithPath("courseType").description("교양/전공여부").attributes(key("constraint").value("전공 : CC / 교양 : GC")),
                                fieldWithPath("score").description("강의학점")
                        ),
                        responseFields(
                                fieldWithPath("id").description("강의Id")
                        )
                ));
    }

    @Test
    @DisplayName("수강신청")
    void applyCourse() throws Exception {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(requestDto));
        Member savedMember = memberRepository.save(createMember());

        CourseApply courseApply = CourseApply.of(savedCourse.getId(),savedCourse.getCode(),savedCourse.getMaxCount(), savedMember.getUsername());

        String json = objectMapper.writeValueAsString(courseApply);


        //when & them
        mvc.perform(RestDocumentationRequestBuilders.post("/course/apply")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("applyCourse",
                        requestFields(
                                fieldWithPath("courseId").description("강의Id"),
                                fieldWithPath("maxCount").description("수강정원"),
                                fieldWithPath("code").description("강의코드").optional(),
                                fieldWithPath("memberId").description("회원Id").optional(),
                                fieldWithPath("username").description("회원코드")
                        ),
                        responseFields(
                                fieldWithPath("message").description("수강신청 되었습니다.")
                        )
                ));

    }



    private Member createMember(int i) {
        return Member.builder()
                .username("HANATEST"+i)
                .password("123456")
                .name("HANATEST"+i)
                .maxScore(21)
                .memberType(MemberType.STUDENT)
                .build();
    }
    private Member createMember() {
        return Member.builder()
                .username("HANATEST")
                .password("123456")
                .name("HANATEST")
                .memberType(MemberType.STUDENT)
                .currentScore(0)
                .maxScore(21)
                .build();
    }

}
