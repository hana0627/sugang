package com.hana.sugang.api.course.dto.request;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;

/**
 * Course 요청 Dto타입
 */
public record CourseCreate(
        String code, // 강의코드
        String title, // 강의명
        String description, // 강의설명
        Integer maxCount, // 최대 수강가능 인원 수 
        CourseType courseType, //전공, 교양 여부
        Integer score

) {


    public static CourseCreate of(String code, String title, String description, Integer maxCount, CourseType courseType, Integer score) {
        return new CourseCreate(code, title, description, maxCount, courseType, score);
    }
    public static Course toEntity(CourseCreate requestDto) {
        return Course.builder()
                .code(requestDto.code)
                .title(requestDto.title)
                .description(requestDto.description)
                .maxCount(requestDto.maxCount)
                .courseType(requestDto.courseType)
                .score(requestDto.score)
                .build();
    }



}
