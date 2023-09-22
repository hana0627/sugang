package com.hana.sugang.global.exception;

/**
 * 커스텀예외의 최상위 클래스
 */
public abstract class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }

    public abstract String getStatusCode();
}
