package com.hana.sugang.global.exception;

public class MemberDuplicateException extends CustomException{

    private static final String MESSAGE = "중복된 회원코드 입니다.";

    public MemberDuplicateException() {
        super(MESSAGE);
    }

    // 409 Conflict
    // 응답 상태 코드는 서버의 현재 상태와 요청이 충돌했음을 의미
    // https://developer.mozilla.org/ko/docs/Web/HTTP/Status/409
    @Override
    public String getStatusCode() {
        return "409";
    }

}
