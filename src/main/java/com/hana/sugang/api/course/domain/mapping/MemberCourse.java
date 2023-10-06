package com.hana.sugang.api.course.domain.mapping;

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
    private Course course; // 강의 Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 회원Id


    public static MemberCourse of(Course course, Member member) {
        return new MemberCourse(course, member);
    }

    public MemberCourse(Course course, Member member) {
        this.course = course;
        this.member = member;
    }
}
