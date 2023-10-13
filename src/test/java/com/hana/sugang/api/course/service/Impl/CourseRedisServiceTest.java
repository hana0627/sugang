package com.hana.sugang.api.course.service.Impl;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.mapping.MemberCourseRepository;
import com.hana.sugang.api.course.repository.redis.CourseCountRepository;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import com.hana.sugang.global.exception.MaxCountException;
import com.hana.sugang.global.exception.MemberNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DisplayName("CourseService 테스트 - REDIS")
class CourseRedisServiceTest {

    @Autowired
    private CourseRedisService courseRedisService;

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

    @BeforeEach
    void before() {
        courseCountRepository.flushAll();
        memberRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @AfterEach
    void after() {
        courseCountRepository.flushAll();
        memberRepository.deleteAll();
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
        List<CourseResponse> courses = courseRedisService.findCourses(courseSearch);

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
        CourseResponse result = courseRedisService.findOne(savedEntity.getId());

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
            courseRedisService.findOne(9999L);
        });


    }

    @Test
    @DisplayName("강의등록")
    void saveCourse() {
        //given
        long before = courseRepository.count();
        CourseCreate requestDto = CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30, CourseType.CC,3 );


        //when
        courseRedisService.saveCourse(requestDto);


        //then
        long after = courseRepository.count();
        assertThat(after).isEqualTo(before+1);

    }


    @Test
    @DisplayName("강의 수정 성공케이스")
    void editCourse() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        CourseEdit courseEdit = createCourseEdit();

        //when
        //"테스트수정강의","설명입니다.수정.",33, courseType,2
        courseRedisService.editCourse(savedCourse.getId(), courseEdit);

        //then
        Course course = courseRepository.findById(savedCourse.getId()).orElseThrow(CourseNotFoundException::new);

        assertThat(course.getTitle()).isEqualTo("테스트수정강의");
        assertThat(course.getDescription()).isEqualTo("설명입니다.수정.");
        assertThat(course.getMaxCount()).isEqualTo(33);
        assertThat(course.getCourseType()).isEqualTo(CourseType.CC);
        assertThat(course.getScore()).isEqualTo(2);
    }


    @Test
    @DisplayName("강의 수정- 제목이 공백이면 제목을 제외하고 수정된다.")
    void editCourseWithNoTitle() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        CourseEdit courseEdit = createCourseEditNoTitle();

        //when
        courseRedisService.editCourse(savedCourse.getId(), courseEdit);

        //then
        Course course = courseRepository.findById(savedCourse.getId()).orElseThrow(CourseNotFoundException::new);

        assertThat(course.getTitle()).isEqualTo("테스트등록강의");
        assertThat(course.getDescription()).isEqualTo("설명입니다.수정.");
        assertThat(course.getMaxCount()).isEqualTo(33);
        assertThat(course.getCourseType()).isEqualTo(CourseType.CC);
        assertThat(course.getScore()).isEqualTo(2);
    }


    @Test
    @DisplayName("강의 삭제 성공케이스")
    void deleteCourse() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        long before = courseRepository.count();

        //when
        courseRedisService.deleteCourse(savedCourse.getId());

        //then
        long after = courseRepository.count();
        assertThat(after).isEqualTo(before-1);
        em.clear();
    }

    @Test
    @DisplayName("강의 삭제시 수강신청한 학생이 있는경우")
    void deleteCourseWithApplyStudent() {
        //given
        Course initCourse = courseRepository.save(createCourse());
        for(int i = 1 ; i<=25 ; i++) {
            Member initMember = memberRepository.save(createMember(i));
            initCourse.addCurrentCount();
            initMember.addCurrentScore(initCourse.getScore());
            Course savedCourse = courseRepository.save(initCourse);
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
                e-> assertThat(e.getCurrentScore()).isEqualTo(initCourse.getScore())
        );
        assertThat(initCourse.getScore()).isEqualTo(3);
        // 테스트데이터 검증 - end


        //when
        courseRedisService.deleteCourse(initCourse.getId());

        //then
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
    }



    @Test
    @DisplayName("수강신청 성공케이스")
    void applyValidationTest() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        Member savedMember = memberRepository.save(createMember());
        CourseApply courseApply = CourseApply.of(savedCourse.getId(),savedCourse.getCode(),savedCourse.getMaxCount(), savedMember.getUsername());

        long before = memberCourseRepository.count();
        Integer beforeCount = savedCourse.getCurrentCount();
        Integer beforeScore = savedMember.getCurrentScore();

        //when
        courseRedisService.applyCourse(courseApply);

        //then
        long after = memberCourseRepository.count();
        Integer afterCount = courseRepository.findById(savedCourse.getId()).orElseThrow(CourseNotFoundException::new).getCurrentCount();
        Integer afterScore = memberRepository.findByUsername(savedMember.getUsername()).orElseThrow(MemberNotFoundException::new).getCurrentScore();

        //매핑테이블 갯수 1 증가
        assertThat(after).isEqualTo(before+1);
        //수강신청인원수는 1증가
        assertThat(afterCount).isEqualTo(beforeCount+1);
        // 현재학점은 강의학점만큼 증가
        assertThat(afterScore).isEqualTo(beforeScore+savedCourse.getScore());

        em.clear();
    }


    @Test
    @DisplayName("수강신청 - 강의정원이 다차면 예외발생")
    @Transactional
    void applyValidationTestWithMaxCount() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        Member savedMember = memberRepository.save(createMember(99999));
        for(int i = 0; i<savedCourse.getMaxCount(); i++) {
            courseCountRepository.increment(savedCourse.getCode());
        }

        CourseApply courseApply = CourseApply.of(savedCourse.getId(),savedCourse.getCode(),savedCourse.getMaxCount(), savedMember.getUsername());

        //when & then
        assertThrows(MaxCountException.class, ()-> {
            courseRedisService.applyCourse(courseApply);
        });

        em.clear();
    }

    @Test
    @DisplayName("수강신청 - 수강가능 학점이 초과하면 예외발생")
    @Transactional
    void applyValidationTestWithMaxScore() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        Member savedMember = memberRepository.save(createMember(33));
        savedMember.MaxCurrentScoreFORTEST();


        CourseApply courseApply = CourseApply.of(savedCourse.getId(),savedCourse.getCode(),savedCourse.getMaxCount(), savedMember.getUsername());


        //when & then
        assertThrows(MaxCountException.class, ()-> {
            courseRedisService.applyCourse(courseApply);
        });

        em.clear();
    }


    /**
     * mutilThread환경 테스트
     */
    @Test
    @DisplayName("동시에 500개의 요청이 한개의 강의에 요청을 보내는 경우")
    void current500request() throws Exception {
        int threadCount = 500;
        // 고정된 쓰레드풀을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Course savedCourse = courseRepository.save(createCourse()); // 강의생성

        for(int i = 0 ;i<threadCount; i++) {
            memberRepository.save(createMember(i));
            CourseApply courseApply = CourseApply.of(savedCourse.getId(),savedCourse.getCode(),savedCourse.getMaxCount(), "HANATEST"+i);
            executorService.submit(() -> {
                try {
                    courseRedisService.applyCourse(courseApply);
                }
                finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Course findCourse = courseRepository.findById(savedCourse.getId()).orElseThrow(CourseNotFoundException::new);
        // (현재 수강신청 인원수).isEqualTo(최대 수강가능 인원수)
        assertThat(findCourse.getCurrentCount()).isEqualTo(findCourse.getMaxCount());

        em.clear();// testData가 DB에 반영되는 현상이 있어서 강제초기화
    }



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

    private CourseCreate createCourseCreate() {
        return CourseCreate.of("ZZZZ01","테스트등록강의","설명입니다.",30,CourseType.CC,3);
    }

    private CourseEdit createCourseEdit() {
        return CourseEdit.of("테스트수정강의","설명입니다.수정.",33, CourseType.CC,2);
    }

    private CourseEdit createCourseEditNoTitle() {
        return CourseEdit.of(null,"설명입니다.수정.",33, CourseType.CC,2);
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

    private Member createMember(int i) {
        return Member.builder()
                .username("HANATEST"+i)
                .password("123456")
                .name("HANATEST"+i)
                .maxScore(21)
                .memberType(MemberType.STUDENT)
                .build();
    }

}
