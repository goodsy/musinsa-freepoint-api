package com.musinsa.freepoint.common.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "idempotency_keys")
public class IdempotencyKey {
    @Id
    @Column(name = "idempotency_key", length = 200, nullable = false, columnDefinition = "varchar(255) comment '멱등성 키 (Idempotency-Key 헤더 값)'")
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default current_timestamp comment '키 등록 시각'")
    private LocalDateTime createdAt = LocalDateTime.now();

    protected IdempotencyKey() {}

    public IdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}