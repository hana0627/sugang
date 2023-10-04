package com.hana.sugang.api.course.dto.mapping;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.member.domain.Member;

/**
 * Course 엔티티
 * Member 엔티티 정보를 담고있는 dto
 */
public record MemberCourseDto(
        Course course,
        Member member
) {

    public static MemberCourseDto of(Course course, Member member) {
        return new MemberCourseDto(course, member);
    }
}
