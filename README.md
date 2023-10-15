# sugang - 내손으로 만드는 수강신청 프로젝트

대용량 트래픽상황을 고려한 수강신청 프로젝트입니다.

---

## 개발 환경

* Intellij IDEA Ultimate
* Java 17
* Gradle 8.2.1
* Spring Boot 3.1.4

---

## 기술 세부 스택

Spring Boot

* Spring Web
* Spring Data JPA
* kafka
* RestAPI
* RestDocs
* Spring Security
* Redis
* MariaDB
* Docker-compose
* Lombok

---

## 개발기간

1차 : 2023.09.23 ~ 2023.10.15

2차 : [Elasticsearch]를 이용한 검색성능 최적화구현


---

## 주요기능

* Redis, Kafka를 활용하여 부하가 많은 상황에서 애플리케이션 안정성, 고성능 유지가 가능한 Application 제작
* 회원 / 강의에 대한 CRUD
* 및 수강신청, 수강취소, 강의삭제 등의 비지니스 로직 구현

---

## 프로젝트 중 트러블 슈팅

JPA 성능최적화 문제 해결 - N+1문제 및 벌크연산문제 해결
[https://velog.io/@hana0627/JPA로-성능최적화-이끌어내기](https://velog.io/@hana0627/JPA%EB%A1%9C-%EC%84%B1%EB%8A%A5%EC%B5%9C%EC%A0%81%ED%99%94-%EC%9D%B4%EB%81%8C%EC%96%B4%EB%82%B4%EA%B8%B0)

구축된 프로젝트에 RestDocs도입하기
[https://velog.io/@hana0627/프로젝트에-RestDocs-적용하기](https://velog.io/@hana0627/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90-RestDocs-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0)

성능향상을 위해 Redis를 도입하였으나, 오히려 성능이 나빠지는 문제 해결
[https://velog.io/@hana0627/REDIS를-이용하여-성능향상하기](https://velog.io/@hana0627/REDIS%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%EC%84%B1%EB%8A%A5%ED%96%A5%EC%83%81%ED%95%98%EA%B8%B0)

QueryDsl + PessimisticLock활용 - DeadLock 발생 / Locking이 의도대로 동작되지 않던 문제 해결
[https://velog.io/@hana0627/JPA-QueryDSL-사용시-DB-Lock이-제대로-되지-않는-현상](https://velog.io/@hana0627/JPA-QueryDSL-%EC%82%AC%EC%9A%A9%EC%8B%9C-DB-Lock%EC%9D%B4-%EC%A0%9C%EB%8C%80%EB%A1%9C-%EB%90%98%EC%A7%80-%EC%95%8A%EB%8A%94-%ED%98%84%EC%83%81)

 멀티스레드환경에서 SpringbootTest시 트랜잭션의 의도대로 동작하지 않던 문제 해결
[https://velog.io/@hana0627/JPA-멀티스레드-환경에서의-테스트코드-작성중-의도와-다른-에러가-나오는-현상](https://velog.io/@hana0627/JPA-%EB%A9%80%ED%8B%B0%EC%8A%A4%EB%A0%88%EB%93%9C-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C%EC%9D%98-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1%EC%A4%91-%EC%9D%98%EB%8F%84%EC%99%80-%EB%8B%A4%EB%A5%B8-%EC%97%90%EB%9F%AC%EA%B0%80-%EB%82%98%EC%98%A4%EB%8A%94-%ED%98%84%EC%83%81)


---

## 프로젝트 회고
https://velog.io/@hana0627/SUGANG-%EB%82%B4%EA%B0%80%EB%A7%8C%EB%93%A0%EC%88%98%EA%B0%95%EC%8B%A0%EC%B2%AD-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%9A%8C%EA%B3%A0
