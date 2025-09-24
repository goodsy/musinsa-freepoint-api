
# musinsa-freepoint-api

Java 21 · Spring Boot 3.x · H2 · Hexagonal Architecture · Swagger/OpenAPI · Idempotency-Key · AOP API Log

## Quick Start
```bash
./gradlew clean bootRun
# Swagger UI: http://localhost:8080/swagger-ui/index.html
# Health:     http://localhost:8080/api/v1/points/health
# H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:freepoint)
```

## Endpoints (MVP)
- `POST /api/v1/points/accruals` : 적립 (body: userId, amount, expiryDays?, manual?)
- `POST /api/v1/points/usages` : 사용 (body: userId, orderNo, amount)
- `POST /api/v1/points/usages/{usageId}/cancel` : 사용취소 (body: amount, reason)

> 멱등성: POST 요청에 `Idempotency-Key` 헤더를 보내면 재호출 시 저장된 응답을 반환합니다.

## ERD & Architecture
- `src/main/resources/erd/erd.puml`
- `src/main/resources/erd/architecture.puml`

## Notes
- 보안(JWT)은 데모 편의를 위해 `permitAll`이며, Resource Server나 커스텀 JWT Filter 추가로 쉽게 강화 가능합니다.
- 정책 값은 `application.yml`에서 설정(추후 DB 정책 테이블 override 추가 가능).
- Flyway가 부트 시 스키마를 생성합니다.
