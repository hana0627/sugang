package com.hana.sugang.api.member.domain;

import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.global.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member  extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10, nullable = false)
    private String memberCode; // 학생코드(학번 등으로 이용)
    @Column(length = 20, nullable = false)
    private String password; //비밀번호
    
    @Column(length = 30)
    private String username; // 이름

    private MemberType memberType;

    private Integer currentScore; // 현재 신청한 강의수
    private Integer maxScore; // 신청 가능한 강의수
}
