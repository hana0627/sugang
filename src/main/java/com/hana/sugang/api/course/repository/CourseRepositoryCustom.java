package com.hana.sugang.api.course.repository;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.dto.request.CourseSearch;

import java.util.List;
import java.util.Optional;

public interface CourseRepositoryCustom {
    List<Course> getList(CourseSearch courseSearch);

    Optional<Course> findBYIdWithQuery(Long id);

    Optional<Course> getCourseWithFetchJoin(Long id);

    Optional<Course> findByIdRedis(Long id);
}
