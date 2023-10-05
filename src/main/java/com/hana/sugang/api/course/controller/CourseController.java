package com.hana.sugang.api.course.controller;

import com.hana.sugang.api.course.dto.mapping.MemberCourseDto;
import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String,Long> createCourse(@RequestBody @Validated CourseCreate requestDto) {
        Map<String, Long> map = new HashMap<>();
        Long id = courseService.saveCourse(requestDto);

        map.put("id",id);
        return map;

    }

    /**
     * 학생이 수강 신청
     */
    @PostMapping("/course/apply")
    public Map<String,String> applyCourse(@RequestBody CourseApply requestDto) {
        Map<String, String> map = new HashMap<>();

        // 수강가능 여부를 확인
        String message = courseService.applyCourse(requestDto);

        // 수강신청
//        String message = courseService.applyCourse(memberCourseDto);

        map.put("message",message);

        return map;
    }
}
