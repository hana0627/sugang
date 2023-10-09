package com.hana.sugang.api.course.dto.request;

import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.global.valid.ValidEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 강의 수정 Dto 객체
 */
public record CourseEdit(

        @NotBlank(message = "강의명을 입력해주세요.")
        String title, // 강의명
        String description, // 강의설명
        @Max(value = 100,message = "수강인원은 100명을 넘을 수 없습니다.")
        Integer maxCount, // 최대 수강가능 인원 수
        @ValidEnum(enumClass = CourseType.class, message = "전공, 교양 여부를 선택해주세요.")
        CourseType courseType, //전공, 교양 여부
        @Min(value = 1, message = "학점은 1 보다 낮을 수 없습니다.")
        @Max(value = 3, message = "학점은 3보다 높을 수 없습니다.")
        Integer score
) {

    public static CourseEdit of(String title, String description, Integer maxCount, CourseType courseType, Integer score) {
        return new CourseEdit(title, description, maxCount, courseType, score);
    }
    public static Course toEntity(CourseEdit requestDto) {
        return Course.builder()
                .title(requestDto.title)
                .description(requestDto.description)
                .maxCount(requestDto.maxCount)
                .courseType(requestDto.courseType)
                .score(requestDto.score)
                .build();
    }
}
