package com.hana.sugang.api.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.mapping.MemberCourseRepository;
import com.hana.sugang.api.course.repository.redis.CourseCountRepository;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import jakarta.persistence.EntityManager;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    private CourseCountRepository courseCountRepository;
    @Autowired
    private EntityManager em;

    @Autowired
    private ObjectMapper objectMapper;

    public CourseControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }


    @BeforeEach
    void before() {
        memberRepository.deleteAll();
        courseRepository.deleteAll();
        courseCountRepository.flushAll();
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
    @AfterEach
    void after() {
        memberRepository.deleteAll();
        courseRepository.deleteAll();
        courseCountRepository.flushAll();
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
    @DisplayName("강의정보 수정 성공케이스")
    void editCourse() throws Exception {
        //given
        CourseCreate createCourse = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(createCourse));

        CourseEdit editCourse = CourseEdit.of("ZZZZ02", "테스트강의수정", 33, CourseType.GC, 2);
        String json = objectMapper.writeValueAsString(editCourse);

        //when & then
        mvc.perform(patch("/course/"+savedCourse.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        Course resultCourse = courseRepository.findById(savedCourse.getId()).orElseThrow(ClassNotFoundException::new);
        assertThat(resultCourse.getTitle()).isEqualTo("ZZZZ02");
        assertThat(resultCourse.getDescription()).isEqualTo("테스트강의수정");
        assertThat(resultCourse.getMaxCount()).isEqualTo(33);
        assertThat(resultCourse.getCourseType()).isEqualTo(CourseType.GC);
        assertThat(resultCourse.getScore()).isEqualTo(2);

    }

    @Test
    @DisplayName("강의정보 수정시 교양/전공 여부는 필수이다.")
    void editCourseWithNoCourseType() throws Exception {
        //given
        CourseCreate createCourse = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(createCourse));

        CourseEdit editCourse = CourseEdit.of("ZZZZ02", "테스트강의수정", 33, null, 2);
        String json = objectMapper.writeValueAsString(editCourse);

        //when & then
        mvc.perform(patch("/course/"+savedCourse.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.validation.courseType").value("전공, 교양 여부를 선택해주세요."))
                .andDo(print());
    }

    
    @Test
    @DisplayName("강의삭제 성공케이스")
    @Transactional
    void deleteCourse() throws Exception {
        //given
        CourseCreate createCourse = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedCourse = courseRepository.save(CourseCreate.toEntity(createCourse));
        long before = courseRepository.count();

        //when & then
        mvc.perform(delete("/course/"+savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print());

        long after = courseRepository.count();
        assertThat(after).isEqualTo(before-1);

        // 삭제한 강의로 조회시 예외발생
        assertThrows(CourseNotFoundException.class, () -> {
            courseRepository.findById(savedCourse.getId()).orElseThrow(CourseNotFoundException::new);
        });
    }

    @Test
    @DisplayName("강의삭제- 존재하지 않는강의 삭제시 에러메세지를 출력한다.")
    @Transactional
    void deleteCourseWithNoId() throws Exception {
        //given
        //Nothing

        //when & then
        mvc.perform(delete("/course/"+9999999))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("강의를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("강의 삭제시 수강신청이 된 학생이 있는 경우.")
    //TODO 서비스코드테스트 우선 작성 후 이후 상황에 맞추어 정리하기
    void deleteCourseWithApplyStudent() throws Exception {
        //given
        Course course = courseRepository.save(createCourse());
        for(int i = 1 ; i<=25 ; i++) {
            Member initMember = memberRepository.save(createMember(i));
            course.addCurrentCount();
            initMember.addCurrentScore(course.getScore());
            Course savedCourse = courseRepository.save(course);
            Member savedMember = memberRepository.save(initMember);
            MemberCourse memberCourse = MemberCourse.of(savedCourse, savedMember);
            memberCourseRepository.save(memberCourse);
        }

        long beforeCourseCount = courseRepository.count();
        long beforeMC = memberCourseRepository.count();
        // 테스트데이터 검증 - start
        assertThat(beforeCourseCount).isEqualTo(1); // 강의는 한건 생성
        assertThat(beforeMC).isEqualTo(25); // 강의-학생 매핑은 25건 생성
        List<Member> beforeMembers = memberRepository.findAll();
        beforeMembers.forEach(
                // 모든 학생의 신청학점은 강의의 학점과 동일
                e-> assertThat(e.getCurrentScore()).isEqualTo(course.getScore())
        );
        assertThat(course.getScore()).isEqualTo(3);
        // 테스트데이터 검증 - end

        //when & then
        mvc.perform(delete("/course/"+course.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andDo(print());


        long afterMC = memberCourseRepository.count();
        long afterCourseCount = courseRepository.count();
        List<Member> afterMembers = memberRepository.findAll();
        afterMembers.forEach(e -> {
            //학생 학점 초기화
            assertThat(e.getCurrentScore()).isEqualTo(0);
        });
        // 학생수는 변함 없음
        assertThat(afterMembers.size()).isEqualTo(25);
        // 매핑테이블 삭제
        assertThat(afterMC).isEqualTo(0);
        // 강의테이블 삭제
        assertThat(afterCourseCount).isEqualTo(0);

        //삭제한 강의로 조회시 예외발생
        assertThrows(CourseNotFoundException.class, () -> {
            courseRepository.findById(course.getId()).orElseThrow(CourseNotFoundException::new);
        });
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

        /**
         * redis환경테스트 추가 - RDB사용시 주석필요
         */
        for(int i = 0; i < savedCourse.getMaxCount(); i++) {
            courseCountRepository.increment(savedCourse.getCode());
        }

        
        
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
    
    /**
     * mutilThread환경 테스트
     */
    @Test
    @DisplayName("동시에 100개의 요청이 한개의 강의에 요청을 보내는 경우")
    void current100request() throws Exception {
        //given

        //when

        //then

    }

    private Course createCourse() {
        return Course.builder()
                .code("ZZZZ01")
                .title("테스트등록강의")
                .description("설명입니다.")
                .maxCount(30)
                .courseType(CourseType.CC)
                .score(3)
                .build();
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
