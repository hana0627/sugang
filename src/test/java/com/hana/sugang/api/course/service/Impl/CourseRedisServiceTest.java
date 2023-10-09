package com.hana.sugang.api.course.service.Impl;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DisplayName("CourseService 테스트")
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
    @DisplayName("수강신청 성공케이스")
    void applyValidationTest() {
        //given
        Course savedCourse = courseRepository.save(createCourse());
        Member savedMember = memberRepository.save(createMember());
        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());

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
        savedCourse.maxCurrentCountFORTEST();


        /**
         * redis환경테스트 추가 - RDB사용시 주석필요
         */
        for(int i = 0; i < savedCourse.getMaxCount(); i++) {
            courseCountRepository.increment(savedCourse.getCode());
        }
        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());

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


        CourseApply courseApply = CourseApply.of(savedCourse.getId(), savedMember.getUsername());


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
    @DisplayName("동시에 100개의 요청이 한개의 강의에 요청을 보내는 경우")
    void current100request() throws Exception {
        int threadCount = 100;
        // 고정된 쓰레드풀을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Course savedCourse = courseRepository.save(createCourse()); // 강의생성

        for(int i = 0 ;i<threadCount; i++) {
            memberRepository.save(createMember(i));
            CourseApply courseApply = CourseApply.of(savedCourse.getId(), "HANATEST" + i);
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
