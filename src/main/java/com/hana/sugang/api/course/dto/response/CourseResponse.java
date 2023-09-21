package com.hana.sugang.api.course.dto.response;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import lombok.Builder;

/**
 * Course 응답 객체
 */
@Builder
public record CourseResponse(
        Long id,
        String code, // 강의코드
        String title, // 강의 명
        String description, // 강의 설명
        CourseType courseType, // 전공,교양 여부
        Integer score, // 학점
        Integer maxCount, // 최대 수강가능 인원 수
        Integer currentCount // 현재 수강신청한 인원 수
) {

    public static CourseResponse from(Course entity) {
        return CourseResponse.builder()
                .code(entity.getCode())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .courseType(entity.getCourseType())
                .score(entity.getScore())
                .maxCount(entity.getMaxCount())
                .currentCount(entity.getCurrentCount())
                .build();
    }
}
