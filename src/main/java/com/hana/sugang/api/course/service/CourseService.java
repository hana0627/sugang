package com.hana.sugang.api.course.service;

import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.hana.sugang.api.course.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {
    List<CourseResponse> findCourses(CourseSearch courseSearch);
    CourseResponse findOne(Long id);
    Long saveCourse(CourseCreate requestDto);
    String applyCourse(CourseApply requestDto);
    Long editCourse(Long id, CourseEdit requestDto);
    void deleteCourse(Long id);
}
