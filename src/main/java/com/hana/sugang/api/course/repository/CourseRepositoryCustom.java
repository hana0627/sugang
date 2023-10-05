package com.hana.sugang.api.course.repository;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.dto.request.CourseSearch;

import java.util.List;

public interface CourseRepositoryCustom {
    List<Course> getList(CourseSearch courseSearch);
}
