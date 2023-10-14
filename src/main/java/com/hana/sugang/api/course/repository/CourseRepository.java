package com.hana.sugang.api.course.repository;

import com.hana.sugang.api.course.domain.Course;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
    @Lock(LockModeType.NONE)
    Optional<Course> findById(Long courseId);
}
