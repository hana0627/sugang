package com.hana.sugang.api.course.domain;


import com.hana.sugang.api.course.domain.constant.CourseType;
import com.hana.sugang.api.course.domain.constant.CourseTypeConverter;
import com.hana.sugang.api.course.domain.mapping.MemberCourse;
import com.hana.sugang.api.course.dto.request.CourseEdit;
import com.hana.sugang.global.domain.AuditingFields;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Course extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 50)
    private String code; // 교과목 코드

    @Column(nullable = false, length = 50)
    private String title; // 과목명
    @Column(length = 500)
    private String description; //과목설명
    @Column(nullable = false, length = 50)
    private Integer maxCount; // 최대 수강신청가능 인원 수
    @ColumnDefault("0")
    private Integer currentCount;// 현재 수강신청 인원 수


    @Convert(converter = CourseTypeConverter.class)
    @Column(nullable = false, length = 10)
    private CourseType courseType; //교양, 전공여부

    private Integer score; // 학점


    @OneToMany(mappedBy = "course", cascade = {CascadeType.ALL})
    private List<MemberCourse> memberCourses = new ArrayList<>();


    @Builder
    public Course(String code, String title, String description, Integer maxCount, Integer currentCount, CourseType courseType, Integer score) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.maxCount = maxCount;
        this.currentCount = currentCount;
        this.courseType = courseType;
        this.score = score;
    }


    @PrePersist
    private void columnDefault() {
        this.currentCount = 0;
    }


    /**
     * 강의 마감여부 체크 메소드
     * @return 현재 신청인원이 정원보다 많으면 return true 그렇지 않으면 return false
     */
    public boolean isFulled() {
        return this.currentCount >= this.maxCount;
    }


    /**
     * 수강신청시 현재신청인원 +1
     */
    public void addCurrentCount() {
        this.currentCount += 1;
    }

    //TODO 사용금지
    /**
     * 테스트편의를 위한 상태변경메소드
     * 테스트코드 외 절대사용금지
     */
    public void maxCurrentCountFORTEST() {
        this.currentCount = this.maxCount;
    }

    /**
     * 상태변경 메소드
     * 필드값이 null이면 기본값 유지, 그렇지않으면 변경
     * @param requestDto
     */
    // TODO 빈값 체크를 위해 if문이 계속 붙어있다.
    //  더 세련되게 처리할 수있을 것 같은데 생각이 잘 안남..
    public void edit(CourseEdit requestDto) {
        if (hasText(requestDto.title())) {
            this.title = requestDto.title();
        }
        if (hasText(requestDto.description())) {
            this.description = requestDto.description();
        }
        if (requestDto.maxCount() != null) {
            this.maxCount = requestDto.maxCount();
        }
        if (requestDto.courseType() != null) {
            this.courseType = requestDto.courseType();
        }
        if (requestDto.score() != null) {
            this.score = requestDto.score();
        }
    }
}
