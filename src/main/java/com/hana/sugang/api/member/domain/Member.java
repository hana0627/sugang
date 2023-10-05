package com.hana.sugang.api.member.domain;

import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.member.domain.constant.MemberType;
import com.hana.sugang.api.member.domain.constant.MemberTypeConverter;
import com.hana.sugang.global.domain.AuditingFields;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member  extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 스프링시큐리티와의 연동에서 네이밍 편의상 unique값을 username으로 통일
    // 실제 회원이름을 나타내는 것이 아니라, 학생코드를 의미
    @Column(length = 20, nullable = false, unique = true)
    private String username; // 학생코드(학번 등으로 이용)

    @Column(length = 100, nullable = false)
    private String password; //비밀번호
    
    @Column(length = 30, nullable = false)
    private String name; // 이름

    @Convert(converter = MemberTypeConverter.class)
    @Column(length = 10)
    private MemberType memberType; // 회원구분

    @ColumnDefault("0")
    private Integer currentScore; // 현재 신청한 강의수
    @ColumnDefault("0")
    private Integer maxScore; // 신청 가능한 강의수

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL})
    private List<MemberCourse> memberCourses = new ArrayList<>();


    @PrePersist
    private void columnDefault() {
        this.currentScore = 0;
    }

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


    /**
     * "현재신청한 학점수 + 신청한 과목의 학점수" 가 신청가능한 학점보다 큰지 확인
     * @return 현재신청한 학점수 + 신청한 과목의 학점수 가 더 크면 ture;
     */
    public boolean isMaxScore(int score) {
        return this.currentScore + score > this.maxScore;
    }


    /**
     * 수강신청시 해당 학점만큼 현재 신청한 학점 증가
     * @param score
     */
    public void addCurrentScore(Integer score) {
        this.currentScore += score;
    }
    
    
    //TODO 사용금지
    /**
     * 테스트편의를 위한 상태변경메소드
     * 테스트코드 외 절대사용금지
     */
    public void MaxCurrentScoreFORTEST() {
        this.currentScore = this.maxScore;
    }

}
