package com.hana.sugang.api.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.mapping.MemberCourseRepository;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.repository.MemberRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("Course Controller 테스트")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class CourseControllerTest {


    @Autowired
    private MockMvc mvc;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberCourseRepository memberCourseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public CourseControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }


    @BeforeEach
    void before() {
        memberRepository.deleteAll();
        courseRepository.deleteAll();
//
//        for(int i = 1; i <=20; i++) {
//            if(i <10 && i %2 == 0) {
//                CourseCreate requestDto = CourseCreate.of("ABCD0"+String.valueOf(i),"강의명"+i ,"설명입니다.",30, CourseType.CC,3 );
//                courseRepository.save(requestDto.toEntity(requestDto));
//            }
//            else if(i <10) {
//                CourseCreate requestDto = CourseCreate.of("ABCD0"+String.valueOf(i),"강의명"+i ,"설명입니다.",30, CourseType.GC,3 );
//                courseRepository.save(requestDto.toEntity(requestDto));
//            }
//            else {
//                CourseCreate requestDto = CourseCreate.of("ABCD"+String.valueOf(i),"강의명"+i ,"설명입니다.",30, CourseType.CC,3 );
//                courseRepository.save(requestDto.toEntity(requestDto));
//            }
//        }

    }
    @Test
    @DisplayName("강의 전체조회 - 별도의 페이징 처리 없을시 1페이지, 최신순 25개를 보여준다.")
    void courseList() throws Exception {
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

        //when & then
        mvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",  Matchers.is(25)))
                .andExpect(jsonPath("$.[0].title").value("강의명50"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print());

    }

    @Test
    @DisplayName("페이지를 0으로 설정하면 첫페이지를 보여준다.")
    void courseListWithPage() throws Exception {
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

        //when & then
        mvc.perform(get("/courses?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",  Matchers.is(25)))
                .andExpect(jsonPath("$.[0].title").value("강의명50"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @DisplayName("페이징 처리시 size및 페이지는 queryString으로 변경 가능하다.")
    void courseListWithQueryString() throws Exception {
        //given
        for(int i = 1; i <=100; i++) {
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

        //when & then
        mvc.perform(get("/courses?page=3&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",  Matchers.is(10)))
                .andExpect(jsonPath("$.[0].title").value("강의명80"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print());

    }

    @Test
    @DisplayName("존재하지 않는 페이지를 강제로 조회시 아무것도 나오지 않는다.")
    //TODO  예외처리 필요성?
    void courseListWithQueryString1() throws Exception {
        //given
        for(int i = 1; i <=100; i++) {
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

        //when & then
        mvc.perform(get("/courses?page=9999&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",  Matchers.is(0)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print());

    }

    @Test
    @DisplayName("강의 단건 조회")
    void findOne() throws Exception {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedEntity = courseRepository.save(CourseCreate.toEntity(requestDto));



        //when & then
        mvc.perform(get("/course/"+savedEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("ZZZZ01"))
                .andExpect(jsonPath("$.title").value("테스트등록강의"))
                .andExpect(jsonPath("$.description").value("설명입니다."))
                .andExpect(jsonPath("$.courseType").value("CC"))
                .andExpect(jsonPath("$.score").value("3"))
                .andExpect(jsonPath("$.maxCount").value("30"))
                .andDo(print());
    }


    @Test
    @DisplayName("존재하지 않는 강의 조회 시 EntityNotFoundException이 발생한다.")
    void findOneError() throws Exception {
        //given

        //when & then
            mvc.perform(get("/course/9999")
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("강의를 찾을 수 없습니다."))
                    .andDo(print());

    }


    @Test
    @DisplayName("강의 등록")
    void saveCourse() throws Exception {
        //given
        long before = courseRepository.count();
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );

        String json = objectMapper.writeValueAsString(requestDto);


        //when
        mvc.perform(post("/course")
                                .contentType(APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        long after = courseRepository.count();

        //then
        assertThat(after).isEqualTo(before+1);


        Course course = courseRepository.findAll().get((int) (after-1));
        assertThat(course.getCode()).isEqualTo("ZZZZ01");
        assertThat(course.getTitle()).isEqualTo("테스트등록강의");
        assertThat(course.getDescription()).isEqualTo("설명입니다.");
        assertThat(course.getMaxCount()).isEqualTo(30);
        assertThat(course.getCourseType()).isEqualTo(CourseType.CC);
        assertThat(course.getScore()).isEqualTo(3);
    }

    @Test
    @DisplayName("강의명이 없으면 강의등록이 이루어지지 않는다.")
    void saveCourseError() throws Exception {
        //given
        long before = courseRepository.count();
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","","설명입니다.",30, CourseType.CC,3 );

        String json = objectMapper.writeValueAsString(requestDto);


        //when & them
        mvc.perform(post("/course")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("강의명을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("교양,전공 여부가 없으면 강의등록에 실패한다.")
    void saveCourseErrorWithNoType() throws Exception {
        //given
//        CourseCreate requestDto = CourseCreate.of("ZZZZ01","강의명","설명입니다.",30, "",3 );
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","강의명","설명입니다.",30, null,3 );


        String json = objectMapper.writeValueAsString(requestDto);


        //when & them
        mvc.perform(post("/course/")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }



    @Test
    @DisplayName("수강신청")
    void applyCourse() throws Exception {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(requestDto));
        Member savedMember = memberRepository.save(createMember());

        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());

        String json = objectMapper.writeValueAsString(courseApply);


        //when & them
        mvc.perform(post("/course/apply")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").value("수강신청 되었습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("수강신청케이스 - 강의정원이 다 찼을경우")
    void applyCourseWithMaxCount() throws Exception {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(requestDto));
        savedCourse.maxCurrentCountFORTEST();

        Member savedMember = memberRepository.save(createMember());

        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());


        String json = objectMapper.writeValueAsString(courseApply);


        //when & them
        mvc.perform(post("/course/apply")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("수강인원이 가득 찼습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("수강신청케이스 - 수강신청 학점이 초과하는경우")
    void applyCourseWithMaxScore() throws Exception {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(requestDto));

        Member savedMember = memberRepository.save(createMember());
        savedMember.MaxCurrentScoreFORTEST();

        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());


        String json = objectMapper.writeValueAsString(courseApply);


        //when & them
        mvc.perform(post("/course/apply")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("신청할 수 있는 학점을 초과했습니다."))
                .andDo(print());
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
