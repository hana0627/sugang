package com.hana.sugang.api.course.service;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

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
    void clean() {
        courseRepository.deleteAll();
    }




    
    @Test
    @DisplayName("강의 전체 조회")
    void findCourses() {
        //given
        //nothing

        //when
        List<CourseResponse> courses = courseService.findCourses();

        //then
        assertThat(courses.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("강의 한개 조회")
    void findOne() {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedEntity = courseRepository.save(CourseCreate.toEntity(requestDto));


        //when
        CourseResponse result = courseService.findOne(savedEntity.getId());


        //then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("ZZZZ01");
        assertThat(result.title()).isEqualTo("테스트등록강의");
        assertThat(result.description()).isEqualTo("설명입니다.");
        assertThat(result.maxCount()).isEqualTo(30);
        assertThat(result.courseType()).isEqualTo(CourseType.CC);
        assertThat(result.score()).isEqualTo(3);

    }



    @Test
    @DisplayName("없는 Id로 조회하면 예외가 발생한다.")
    void findOneError() {
        //given
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Course savedEntity = courseRepository.save(CourseCreate.toEntity(requestDto));


        //when && then
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.findOne(9999L);
        });


        //then

    }

    @Test
    @DisplayName("강의 등록")
    void saveCourse() {
        //given
        long before = courseRepository.count();
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );


        //when
        courseService.saveCourse(requestDto);


        //then
        long after = courseRepository.count();
        assertThat(after).isEqualTo(before+1);

    }


    // 테스트 케이스 작성시 실수한점
    // 필드값에 대한 유효성 검사는 Controller 호출 이전에 수행.
    // Controller 호출 이전이므로 당연히 ServiceTest에서는 유효성 검증 테스트 불가



}