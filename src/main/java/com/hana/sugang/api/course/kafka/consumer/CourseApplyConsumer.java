package com.hana.sugang.api.course.kafka.consumer;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.mapping.MemberCourseRepository;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import com.hana.sugang.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 토픽에 전송된 데이터를 가져오는 Consumer
 */
@Component
@RequiredArgsConstructor
public class CourseApplyConsumer {

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final MemberCourseRepository memberCourseRepository;


    /**
     * 토픽을 읽어서 로직을 수행
     * @param message : 111111&hana
     */
    @KafkaListener(topics = "course_apply", groupId = "group_1")
    public void listener(String message) {
        //message 예시 : 111111&hana
        // & 단어로 분할
        String[] split = message.split("&");
        String courseCode = split[0];
        String username = split[1];


        // TODO
        // 이상하게 이때 실제 객체가 아니라 porxy를 가져옴
        Course course = courseRepository.findByCode(courseCode).orElseThrow(CourseNotFoundException::new);
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);

        // 뭔가 프록시객체여서 of메소드하면 프록시초기화 안된다고 나옴
        MemberCourse memberCourse = new MemberCourse(course, member);
//        MemberCourse memberCourse = MemberCourse.of(course, member);
        memberCourseRepository.save(memberCourse);

        // 변경후 저장 -> 프록시초기화를 위해 save() 메소드추가
        member.addCurrentScore(course.getScore());
        course.addCurrentCount();
        memberRepository.save(member);
        courseRepository.save(course);
    }
}
