package com.musinsa.freepoint.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {
    @Id
    private String key;

    private LocalDateTime createdAt = LocalDateTime.now();

    // 생성자, getter, setter
    protected IdempotencyKey() {}
    public IdempotencyKey(String key) { this.key = key; }
    public String getKey() { return key; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
