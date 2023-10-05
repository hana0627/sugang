package com.hana.sugang.api.course.dto.request;

import lombok.Builder;

/**
 * 강의 조회시 페이징 정보를 담고있는 dto객체
 */
public record CourseSearch(
        Integer page,
        Integer size
) {
    private static final int MAX_SIZE = 2000;
    @Builder
    public CourseSearch(Integer page, Integer size) {
        this.page = page == null ? 1: page;
        this.size = size == null ? 25: size;
    }


    public long getOffset() {
        return (long) (Math.max(1,page) -1) * Math.min(size, MAX_SIZE);
    }
}
