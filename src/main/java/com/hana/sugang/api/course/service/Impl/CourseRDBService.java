package com.hana.sugang.api.course.service.Impl;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.repository.CourseRepository;
import com.hana.sugang.api.course.repository.mapping.MemberCourseRepository;
import com.hana.sugang.api.course.service.CourseService;
import com.hana.sugang.api.member.domain.Member;
import com.hana.sugang.api.member.repository.MemberRepository;
import com.hana.sugang.global.exception.CourseNotFoundException;
import com.hana.sugang.global.exception.MaxCountException;
import com.hana.sugang.global.exception.MemberNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CourseRDBService implements CourseService {

    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final MemberCourseRepository memberCourseRepository;
    private final EntityManager em;


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
     *
     * @param requestDto
     */
    @Transactional
    public String applyCourse(CourseApply requestDto) {
        Course course = courseRepository.findBYIdWithQuery(requestDto.courseId()).orElseThrow(CourseNotFoundException::new);
        Member member = memberRepository.findBYUsernameWithQuery(requestDto.username()).orElseThrow(MemberNotFoundException::new);
        // 학생이 신청가능학점을 초과해서 신청하는경우
        if (member.isMaxScore(course.getScore())) {
            throw new MaxCountException("신청할 수 있는 학점을 초과했습니다.");
        }

        //강의정원이 마감되는경우
        if (course.isFulled()) {
            throw new MaxCountException("수강인원이 가득 찼습니다.");
        }
        MemberCourse memberCourse = MemberCourse.of(course, member);
        memberCourseRepository.save(memberCourse);

        //더티체킹에의해 트랜잭션 종료시 Update
        member.addCurrentScore(course.getScore());
        course.addCurrentCount();
        return "수강신청 되었습니다.";

    }


    /**
     * 강의정보 수정 method
     *
     * @param id
     * @param requestDto
     * @return id
     */
    @Transactional
    public Long editCourse(Long id, CourseEdit requestDto) {
        Course course = courseRepository.findById(id).orElseThrow(CourseNotFoundException::new);

        course.edit(requestDto);

        return course.getId();
    }

    /**
     * 강의정보 삭제 method
     * @param id
     */
    @Transactional
    public void deleteCourse(Long id) {
        // 강의 조회
        Course course = courseRepository.getCourseWithFetchJoin(id).orElseThrow(CourseNotFoundException::new);

        Set<Long> memberIds = new HashSet<>();
        // 강의에 매핑된 리스트만큼 반복문 수행
        course.getMemberCourses().forEach(e -> {
            Member member = memberRepository.findById(e.getMember().getId()).orElseThrow(MemberNotFoundException::new);
            memberIds.add(member.getId());
        });
        // 수강신청한 학생은 강의의 학점만큼 수강신청한 학점 감소
        memberRepository.decreaseCurrentScore(memberIds, course.getScore());
        em.flush();
        em.clear();

        // 매핑테이블 정보 삭제
        memberCourseRepository.deleteAllByCourse(course);
        em.flush();
        em.clear();

        // 강의삭제
        courseRepository.deleteById(id);
    }

}
