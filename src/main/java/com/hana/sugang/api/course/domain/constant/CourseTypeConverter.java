package com.hana.sugang.api.course.domain.constant;

import com.hana.sugang.global.exception.EnumConvertException;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/**
 * 데이터베이스에 이넘타입을 String으로 변경하여 저장해주는 클래스
 *
 */
public class CourseTypeConverter implements AttributeConverter<CourseType,String> {


    /**
     * DB저장시점에 String타입으로 변경
     */
    @Override
    public String convertToDatabaseColumn(CourseType attribute) {
        return attribute.toString();
    }

    /**
     * DB데이터(varchar) -> CourseType으로 변경
     */

    @Override
    public CourseType convertToEntityAttribute(String dbData) {
        return Arrays.stream(CourseType.values())
                .filter(s -> s.toString().equals(dbData))
                .findFirst().orElseThrow(EnumConvertException::new);
    }
}
