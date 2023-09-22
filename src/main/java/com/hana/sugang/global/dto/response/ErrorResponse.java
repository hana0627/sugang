package com.hana.sugang.global.dto.response;

import java.util.Map;

/**
 * 예외상황에 대해서 응답값을 반환해주는 객체
 * {
 *     "code" : "400".
 *     "message" : "잘못된 요청입니다."
 *     "validation" : {
 *         "title" : "강의 제목을 입력해주세요."
 *     }
 * }
 */
public record ErrorResponse(
        String code, //에러코드 (400,404..)
        String message, // 클라이언트에게 보여줄 메세지
        Map<String, String> validation // 필드에러처리
) {

    public static ErrorResponse of(String code, String message, Map<String, String> validation) {
        return new ErrorResponse(code, message, validation);
    }

}