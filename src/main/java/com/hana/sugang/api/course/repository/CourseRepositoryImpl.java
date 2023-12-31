package com.hana.sugang.api.course.repository;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.mapping.QMemberCourse;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.hana.sugang.api.course.domain.QCourse.course;
import static com.hana.sugang.api.course.domain.mapping.QMemberCourse.memberCourse;
import static com.hana.sugang.api.member.domain.QMember.member;

@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    // 전체조회 - paging
    @Override
    public List<Course> getList(CourseSearch courseSearch) {
        return queryFactory.select(course)
                .from(course)
                .limit(courseSearch.size())
                .offset(courseSearch.getOffset())
                .orderBy(course.id.desc())
                .fetch();
    }

    // 단건조회 -- PessimisticLock 이용
    @Override
    public Optional<Course> findBYIdWithQuery(Long id) {
        return Optional.ofNullable(queryFactory.select(course)
                .from(course)
                .where(course.id.eq(id))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne());

    }

    @Override
    public Optional<Course> getCourseWithFetchJoin(Long id) {
        return Optional.ofNullable(queryFactory.selectFrom(course)
                .leftJoin(course.memberCourses, memberCourse).fetchJoin()
                .leftJoin(memberCourse.member, member).fetchJoin()
                .where(course.id.eq(id))
                .fetchOne());
    }

    @Override
    public Optional<Course> findByIdRedis(Long id) {
        return Optional.ofNullable(queryFactory.select(course)
                .from(course)
                .where(course.id.eq(id))
                .setLockMode(LockModeType.NONE)
                .fetchOne());
    }

}
