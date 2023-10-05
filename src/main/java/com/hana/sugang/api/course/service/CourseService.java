package com.hana.sugang.api.course.service;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.mapping.MemberCourseRepository;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import com.hana.sugang.global.exception.MaxCountException;
import com.hana.sugang.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final MemberCourseRepository memberCourseRepository;


    public List<CourseResponse> findCourses(CourseSearch courseSearch) {
        return courseRepository.getList(courseSearch)
                .stream()
                .map(CourseResponse::from)
                .toList();
    }

    /**
     * @param id
     * @return CourseResponse
     */
    public CourseResponse findOne(Long id) {
        Course entity = courseRepository.findById(id).orElseThrow(CourseNotFoundException::new);
        return CourseResponse.from(entity);
    }

    /**
     * @param requestDto
     * @return id
     */
    @Transactional
    public Long saveCourse(CourseCreate requestDto) {
        Course entity = courseRepository.save(CourseCreate.toEntity(requestDto));
        return entity.getId();
    }


    /**
     * 수강신청 가능한지 여부를 확인하고
     * 수강신청이 가능하면 수강신청
     * @param requestDto
     */
    @Transactional
    public String applyCourse(CourseApply requestDto) {
        System.out.println("[CourseService] applyCourse - called");
        long memberCount = memberRepository.count();
        long courseCount = courseRepository.count();
        System.out.println("애플리케이션 memberCount  = " + memberCount);
        System.out.println("애플리케이션 courseCount  = " + courseCount);
        Course course = courseRepository.findById(requestDto.courseId()).orElseThrow(CourseNotFoundException::new);
        Member member = memberRepository.findByUsername(requestDto.username()).orElseThrow(MemberNotFoundException::new);

        // 학생이 신청가능학점을 초과해서 신청하는경우
        if (member.isMaxScore(course.getScore())) {
            throw new MaxCountException("신청할 수 있는 학점을 초과했습니다.");
        }
        // 강의정원이 마감되는경우
        if (course.isFulled()) {
            throw new MaxCountException("수강인원이 가득 찼습니다.");
        }
        
        MemberCourse memberCourse = MemberCourse.of(course, member);
        memberCourseRepository.save(memberCourse);
        
        //신청학점 증가 및 신청인원증가
        member.addCurrentScore(course.getScore());
        course.addCurrentCount();
        
        return "수강신청 되었습니다.";

    }


}
