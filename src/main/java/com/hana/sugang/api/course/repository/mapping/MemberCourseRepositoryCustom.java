package com.hana.sugang.api.course.repository.mapping;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;

import java.util.List;

public interface MemberCourseRepositoryCustom {

    void deleteAllByCourse (Course course);
}
