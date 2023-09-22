package com.hana.sugang.global.valid.impl;

import com.hana.sugang.global.valid.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum> {

    private ValidEnum annotation;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    /**
     *
     * 이넘객체를 전부 조회해서
     * 들어온 타입과 같은타입의 이넘객체가 있다면 return true
     */
    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        boolean result = false;


        Enum<?>[] enumValues = this.annotation.enumClass().getEnumConstants();
        if(enumValues != null) {
            for (Enum<?> enumValue : enumValues) {
                if(value == enumValue) {
                    result = true;
                    break;
                }
            }
        }

        return result;


    }
}
