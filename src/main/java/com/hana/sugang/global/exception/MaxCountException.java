package com.hana.sugang.global.exception;

public class MaxCountException extends CustomException{

    public MaxCountException(String message) {
        super(message);
    }

    // 409 Conflict
    // 응답 상태 코드는 서버의 현재 상태와 요청이 충돌했음을 의미
    // https://developer.mozilla.org/ko/docs/Web/HTTP/Status/409
    @Override
    public String getStatusCode() {
        return "409";
    }
}
