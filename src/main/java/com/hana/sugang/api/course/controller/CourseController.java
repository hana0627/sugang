package com.hana.sugang.api.course.controller;

import com.hana.sugang.api.course.dto.request.CourseApply;
import com.hana.sugang.api.course.dto.request.CourseCreate;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.api.course.dto.request.CourseSearch;
import com.hana.sugang.api.course.dto.response.CourseResponse;
import com.hana.sugang.api.course.service.Impl.CourseRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CourseController {

//    private final CourseRDBService courseRDBService;
    private final CourseRedisService courseRedisService;

    @GetMapping("/courses")
    public List<CourseResponse> courseList(CourseSearch courseSearch) {
        return courseRedisService.findCourses(courseSearch);
    }
    @GetMapping("/course/{id}")
    public CourseResponse course(@PathVariable(name = "id") Long id) {
        return courseRedisService.findOne(id);
    }
    @PostMapping("/course")
    public Map<String,Long> createCourse(@RequestBody @Validated CourseCreate requestDto) {
        Map<String, Long> map = new HashMap<>();
        Long id = courseRedisService.saveCourse(requestDto);

        map.put("id",id);
        return map;
    }
    @PatchMapping("/course/{id}")
    public Map<String, Long> editCourse(@PathVariable(name = "id") Long id, @RequestBody @Validated CourseEdit requestDto) {
        Map<String, Long> map = new HashMap<>();
        Long courseId = courseRedisService.editCourse(id, requestDto);

        map.put("id",courseId);
        return map;
    }
    @DeleteMapping("/course/{id}")
    public Map<String, Long> deleteCourse(@PathVariable(name = "id") Long id) {
        Map<String, Long> map = new HashMap<>();
        courseRedisService.deleteCourse(id);

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
        String message = courseRedisService.applyCourse(requestDto);

        map.put("message",message);

        return map;
    }
}
