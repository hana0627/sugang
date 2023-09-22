package com.hana.sugang.api.member.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원 권한타입
 * 운영자,
 * 매니저(운영자가 일부권한만을 위임. 현재 비지니스에서는 사용할 수도, 안할수도 있음),
 * 교수,
 * 학생,
 * 교직원(현재 비지니스에서는 아마 사용 안할듯)
 *
 */
@RequiredArgsConstructor
public enum MemberType {
    ADMIN("운영자"),
    MANAGER("관리자"),
    PROFESSOR("교수"),
    STUDENT("학생"),
    STAFF("교직원");

    @Getter
    private final String roleName;


}
