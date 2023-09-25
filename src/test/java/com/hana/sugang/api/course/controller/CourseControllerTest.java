package com.hana.sugang.api.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


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
    private ObjectMapper objectMapper;

    public CourseControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }


    @BeforeEach
    void before() {

        for(int i = 1; i <=20; i++) {
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

    }
    @AfterEach
    void after() {
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 전체조회")
    void courseList() throws Exception {
        //given

        //when & then
        mvc.perform(get("/courses"))
                .andExpect(status().isOk())
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
        mvc.perform(post("/course")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.courseType").value("전공, 교양 여부를 선택해주세요."))
                .andDo(print());

    }
}
