package com.hana.sugang.api.member.dto.request;

import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.global.valid.ValidEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * 멤버객체 생성 엔티티
 *
 * 사실 username 같은 Key값은 사용자가 직접 입력하는것이 아니라
 * 내부 코드생성객체에 의해서 생성해야하나
 * 핵관심사가 아니므로 NotNull처리
 */

public record MemberCrate(
        @NotNull
        String username, // 학생코드(학번 등으로 이용)
        @NotNull
        String password, //비밀번호
        @NotNull
        String name, // 이름
        MemberType memberType
) {

        @Builder
        public MemberCrate(String username, String password, String name, MemberType memberType) {
                this.username = username;
                this.password = password;
                this.name = name;
                this.memberType = memberType;
        }
}
