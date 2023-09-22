package com.hana.sugang.api.member.domain;

import com.hana.sugang.api.course.domain.constant.CourseTypeConverter;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.global.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member  extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 스프링시큐리티와의 연동에서 네이밍 편의상 unique값을 username으로 통일
    // 실제 회원이름을 나타내는 것이 아니라, 학생코드를 의미
    @Column(length = 10, nullable = false, unique = true)
    private String username; // 학생코드(학번 등으로 이용)

    @Column(length = 20, nullable = false)
    private String password; //비밀번호
    
    @Column(length = 30)
    private String name; // 이름

    @Convert(converter = CourseTypeConverter.class)
    @Column(nullable = false, length = 10)
    private MemberType memberType;

    private Integer currentScore; // 현재 신청한 강의수
    private Integer maxScore; // 신청 가능한 강의수


    /**
     * 멤버엔티티는 상황에 따라서
     * 생성자로 들어오는 파라미터 수가 많거나.
     * 변경될 필드가 많을것으로 예상되어 Builder패턴을 적용 - 이펙티브자바 Item 2
     */
    @Builder
    public Member(Long id, String username, String password, String name, MemberType memberType, Integer currentScore, Integer maxScore) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.memberType = memberType;
        this.currentScore = currentScore;
        this.maxScore = maxScore;
    }
}
