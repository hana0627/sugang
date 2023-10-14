package com.hana.sugang.api.course.controller;

import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.service.CourseService;
import com.hana.sugang.api.course.service.Impl.CourseRedisService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CourseController {

//    private final CourseRDBService courseRDBService;
    private final CourseService courseService;

    public CourseController(CourseRedisService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<CourseResponse> courseList(CourseSearch courseSearch) {
        return courseService.findCourses(courseSearch);
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
    @PatchMapping("/course/{id}")
    public Map<String, Long> editCourse(@PathVariable(name = "id") Long id, @RequestBody @Validated CourseEdit requestDto) {
        Map<String, Long> map = new HashMap<>();
        Long courseId = courseService.editCourse(id, requestDto);

        map.put("id",courseId);
        return map;
    }
    @DeleteMapping("/course/{id}")
    public Map<String, Long> deleteCourse(@PathVariable(name = "id") Long id) {
        Map<String, Long> map = new HashMap<>();
        courseService.deleteCourse(id);

        map.put("id",id);
        return map;
    }

    /**
     * 학생이 수강 신청
     */
    @PostMapping("/course/apply")
    public Map<String,String> applyCourse(@RequestBody CourseApply requestDto) {
        Map<String, String> map = new HashMap<>();

        // 수강신청
        String message = courseService.applyCourse(requestDto);

        map.put("message",message);

        return map;
    }
}
