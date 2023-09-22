package com.hana.sugang.global.exception;

/**
 * dbValue(varchar) -> Enum
 * 변경 중 예외 발생시
 */
public class EnumConvertException extends CustomException{
    private static final String MESSAGE = "형변환 중 오류가 발생했습니다.";

    public EnumConvertException() {
        super(MESSAGE);
    }

    // 4xx를 주어야 할 지
    // 5xx를 주어야 할 지 고민하였으나
    // 비지니스 로직이 온전하게 작성되었다는 전제 하에,
    // 형변환 불가한 값이 들어오거나 저장되는 경우는 클라이언트 측에서 조작했음을 의미
    // errorCode 400을 return
    @Override
    public String getStatusCode() {
        return "400";
    }
}
