package com.hana.sugang.global.exception;

public class CourseNotFoundException extends CustomException{

    private static final String MESSAGE = "강의를 찾을 수 없습니다.";

    public CourseNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public String getStatusCode() {
        return "404";
    }
}
