package com.hana.sugang.api.member.domain.constant;

import com.hana.sugang.global.exception.EnumConvertException;
import jakarta.persistence.AttributeConverter;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * 데이터베이스에 이넘타입을 String으로 변경하여 저장해주는 클래스
 */
public class MemberTypeConverter  implements AttributeConverter<MemberType,String> {


    /**
     * DB저장시점에 String타입으로 변경
     */
    @Override
    public String convertToDatabaseColumn(MemberType attribute) {
        if(StringUtils.hasText(attribute.toString())) {
            return attribute.toString();
        }
        else {
            return "STUDENT";
        }
    }
    /**
     * DB데이터(varchar) -> CourseType으로 변경
     */
    @Override
    public MemberType convertToEntityAttribute(String dbData) {
        return Arrays.stream(MemberType.values())
                .filter(s -> s.toString().equals(dbData))
                .findFirst().orElseThrow(EnumConvertException::new);
    }
}
