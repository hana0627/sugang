package com.hana.sugang.api.course.service;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.mapping.MemberCourseRepository;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import com.hana.sugang.global.exception.MaxCountException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DisplayName("CourseService 테스트")
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberCourseRepository memberCourseRepository;

    @BeforeEach
    void before() {

        memberRepository.deleteAll();
        courseRepository.deleteAll();


    }

    @AfterEach
    void clean() {
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 첫번째 페이지 조회")
    void findCourses() {
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

        //when
        CourseSearch courseSearch = new CourseSearch(1,25);
        List<CourseResponse> courses = courseService.findCourses(courseSearch);

        //then
        assertThat(courses.size()).isEqualTo(25);
        assertThat(courses.get(0).title()).isEqualTo("강의명100");
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


    }

    @Test
    @DisplayName("강의등록")
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

    @Test
    @DisplayName("수강신청 - validationTest")
    void applyValidationTest() {
        //given
        CourseCreate courseCreate = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );
        Long courseId = courseService.saveCourse(courseCreate);
        Member savedMember = memberRepository.save(createMember());
        CourseApply courseApply = CourseApply.of(courseId, savedMember.getUsername());

        //when
        courseService.applyCourse(courseApply);

        //then
        //Nothing

    }


    @Test
    @DisplayName("수강신청 - 강의정원이 다차면 예외발생")
    void applyValidationTestWithMaxCount() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        Member savedMember = memberRepository.save(createMember());
        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());


        savedCourse.maxCurrentCountFORTEST();

        //when & then
        assertThrows(MaxCountException.class, ()-> {
            courseService.applyCourse(courseApply);
        });
    }

    @Test
    @DisplayName("수강신청 - 수강가능 학점이 초과하면 예외발생")
    void applyValidationTestWithMaxScore() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        Member savedMember = memberRepository.save(createMember());
        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());

        savedMember.MaxCurrentScoreFORTEST();

        //when & then
        assertThrows(MaxCountException.class, ()-> {
            courseService.applyCourse(courseApply);
        });
    }


//    @Test
//    @DisplayName("수강신청이 성공하는 경우")
//    void applyCourseTest() {
//        //given
//        Course savedCourse = courseRepository.save(createCourse());
//        Member savedMember = memberRepository.save(createMember());
//        Integer beforeCount = savedCourse.getCurrentCount();
//        Integer beforeScore = savedMember.getCurrentScore();
//
//        long beforeMC = memberCourseRepository.count();
//
//        MemberCourseDto memberCourseDto = MemberCourseDto.of(savedCourse, savedMember);
//
//
//
//        //when
//        courseService.applyCourse(memberCourseDto);
//
//        //then
//        Integer afterCount = savedCourse.getCurrentCount();
//        Integer afterScore = savedMember.getCurrentScore();
//
//        //수강신청인원수는 1증가
//        assertThat(afterCount).isEqualTo(beforeCount+1);
//        // 현재학점은 강의학점만큼 증가
//        assertThat(afterScore).isEqualTo(beforeScore+savedCourse.getScore());
//
//
//        long afterMC = memberCourseRepository.count();
//        assertThat(afterMC).isEqualTo(beforeMC+1);
//
//        Optional<MemberCourse> optional = memberCourseRepository.findMemberCourseByMemberIdAndCourseId(savedMember.getId(), savedCourse.getId());
//        MemberCourse memberCourse = optional.get();
//        System.out.println(memberCourse);
//
//
//    }
    
    
    
    

    // 테스트 케이스 작성시 실수한점
    // 필드값에 대한 유효성 검사는 Controller 호출 이전에 수행.
    // Controller 호출 이전이므로 당연히 ServiceTest에서는 유효성 검증 테스트 불가


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

    private Member createMember() {
        return Member.builder()
                .username("HANATEST111")
                .password("123456")
                .name("HANATEST111")
                .maxScore(21)
                .memberType(MemberType.STUDENT)
                .build();
    }

}