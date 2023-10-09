package com.hana.sugang.api.course.domain.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hana.sugang.api.course.domain.Course;
import com.hana.sugang.api.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints={
        // 두 필드가 동시에 같은 경우는 없음
        @UniqueConstraint(columnNames = {"course_id", "member_id"})
})
public class MemberCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course; // 강의 Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member; // 회원Id


    public static MemberCourse of(Course course, Member member) {
        MemberCourse memberCourse = new MemberCourse(course, member);

        /**
         * 연관관계 매핑
         */
        course.getMemberCourses().add(memberCourse);
        member.getMemberCourses().add(memberCourse);

        return memberCourse;

    }

    public MemberCourse(Course course, Member member) {
        this.course = course;
        this.member = member;
    }
}
