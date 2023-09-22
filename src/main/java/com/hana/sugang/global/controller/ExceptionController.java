package com.hana.sugang.global.controller;

import com.hana.sugang.global.dto.response.ErrorResponse;
import com.hana.sugang.global.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


/**
 * 전역적인 예외처리를 수행하는 Controller
 */
@RestControllerAdvice
public class ExceptionController {

    /**
     * valid Exceptiom
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse notValidationExceptionHandler(MethodArgumentNotValidException error) {
        Map<String, String> map = new HashMap<>();

        // 필드에러 발생시
        // key : 필드명, value : defaultMessage
        for(FieldError e : error.getFieldErrors()) {
            map.put(e.getField(), e.getDefaultMessage());
        }


        return ErrorResponse.of("400","잘못된 요청입니다.",map);

    }


    /**
     * 공통예외처리 CustomExcetpion
     */
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> CustomExceptionHandler(CustomException e) {
        ErrorResponse errorResponse = ErrorResponse.of(e.getStatusCode(),e.getMessage(), new HashMap<>());


        return ResponseEntity.status(Integer.parseInt(e.getStatusCode()))
                .body(errorResponse);

    }

}
