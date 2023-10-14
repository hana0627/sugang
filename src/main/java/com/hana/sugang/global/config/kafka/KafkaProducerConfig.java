package com.hana.sugang.global.config.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka - Producer를 사용하기 위한 설정파일
 * 토픽에 데이터를 전송하기 위함
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> preferencesFactory() {
        //설정값을 담아줄 변수 Map
        Map<String, Object> config = new HashMap<>();

        //STEP1. 서버의 정보 추가
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        //STEP2. keySerializer 정보 추가
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //STEP3. valueSerializer 정보 추가
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);

        //설정정보를 담은 구현체 return
        return new DefaultKafkaProducerFactory<>(config);
    }


    /**
     * Kafka 토픽에 데이터를 전송하기 위한 Kafka템플릿 생성
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(preferencesFactory());
    }
}
