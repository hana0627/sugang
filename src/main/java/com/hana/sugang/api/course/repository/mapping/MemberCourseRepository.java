package com.hana.sugang.api.course.repository.mapping;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCourseRepository extends JpaRepository<MemberCourse, Long>,MemberCourseRepositoryCustom {

    Optional<MemberCourse> findMemberCourseByMemberIdAndCourseId(Long memberId, Long courseId);

    void deleteByCourse(Course course);

}
