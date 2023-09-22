package com.hana.sugang.api.course.controller;

import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/courses")
    public List<CourseResponse> courseList() {
        return courseService.findCourses();
    }
    @GetMapping("/course/{id}")
    public CourseResponse course(@PathVariable(name = "id") Long id) {
        return courseService.findOne(id);
    }
    @PostMapping("/course")
    public Long createCourse(@RequestBody @Validated CourseCreate requestDto) {
        return courseService.saveCourse(requestDto);
    }

}
