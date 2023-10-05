package com.hana.sugang.api.course.repository;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.QCourse;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.hana.sugang.api.course.domain.QCourse.course;

@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom{
    private final JPAQueryFactory queryFactory;


    @Override
    public List<Course> getList(CourseSearch courseSearch) {
        return queryFactory.select(course)
                .from(course)
                .limit(courseSearch.size())
                .offset(courseSearch.getOffset())
                .orderBy(course.id.desc())
                .fetch();
    }
}
