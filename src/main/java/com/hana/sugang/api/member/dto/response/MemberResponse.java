package com.hana.sugang.api.member.dto.response;


import com.hana.sugang.api.member.domain.constant.MemberType;
import lombok.Builder;

@Builder
public record MemberResponse(
        Long id,
        String username, // 학생코드(학번 등으로 이용)
        String password, //비밀번호
        String name, // 이름
        MemberType memberType,
        Integer currentScore, // 현재 신청한 강의수
        Integer maxScore // 신청 가능한 강의수
) {


}
