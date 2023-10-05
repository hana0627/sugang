package com.hana.sugang.api.course.dto.request;

/**
 * 수강신청시 요청하는 DTO객체
 */
public record CourseApply(
        Long courseId, // 강의Id
        String code, // 강의코드
        Long memberId, //회원 Id
        String username //회원코드
) {

    public static CourseApply of(Long courseId, String username) {
        return new CourseApply(courseId,null,null,username);
    }
}
