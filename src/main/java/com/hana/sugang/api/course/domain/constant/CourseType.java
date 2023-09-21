package com.hana.sugang.api.course.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 교양과목, 전공과목 테스트
 * 전공과목 : CoreClass -> CC
 * 교양과목 : GeneralClass -> GC
 */
@RequiredArgsConstructor
public enum CourseType {
    CC("전공과목"),
    GC("교양과목");

    @Getter
    private final String type;
}
