package com.hana.sugang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SugangApplication {

    public static void main(String[] args) {
        SpringApplication.run(SugangApplication.class, args);
    }

}
