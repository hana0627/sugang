package com.hana.sugang.api.course.repository.mapping;

import com.hana.sugang.api.course.domain.Course;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.hana.sugang.api.course.domain.mapping.QMemberCourse.memberCourse;

@RequiredArgsConstructor
public class MemberCourseRepositoryImpl implements MemberCourseRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAllByCourse(Course course) {
        queryFactory.delete(memberCourse).where(memberCourse.course.eq(course)).execute();
    }
}
