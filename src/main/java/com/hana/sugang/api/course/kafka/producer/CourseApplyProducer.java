package com.hana.sugang.api.course.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


/**
 * Kafka 템플릿을 사용해서 토픽에 데이터를 전달 할 Producer
 */
@Component
@RequiredArgsConstructor
public class CourseApplyProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void create(String courseCode, String username) {
        String message = courseCode + "&" + username;
        kafkaTemplate.send("course_apply", message);
    }

}

