package com.hana.sugang.api.course.repository.mapping;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.course.domain.mapping.QMemberCourse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hana.sugang.api.course.domain.mapping.QMemberCourse.memberCourse;

@RequiredArgsConstructor
public class MemberCourseRepositoryImpl implements MemberCourseRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<MemberCourse> findAllByCourse(Course course) {
        return queryFactory.select(memberCourse)
                .from(memberCourse)
                .where(memberCourse.course.eq(course))
                .fetch();
    }
}
