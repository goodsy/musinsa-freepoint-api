
package com.musinsa.freepoint.domain.accrual;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.musinsa.freepoint.domain.model.Enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Setter
@Getter
@Table(name = "point_accrual")
public class PointAccrual {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private long amount;
    private long remainAmount;
    private boolean manual;
    private String sourceType;
    private String sourceId;
    private Instant expiresAt;
    private String status;
    private String idempotencyKey;
    private String createdBy;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    @Version
    private int version;

    public static PointAccrual create(String userId, long amount, boolean manual, String sourceType, String sourceId, Instant expiresAt) {
        PointAccrual a = new PointAccrual();
        a.userId = userId; a.amount = amount; a.remainAmount = amount;
        a.manual = manual; a.sourceType = sourceType; a.sourceId = sourceId;
        a.expiresAt = expiresAt; a.status = AccrualStatus.ACTIVE.name();
        return a;
    }

    public boolean isExpired(Instant now) { return now.isAfter(expiresAt); }

    public void allocate(long take) {
        if (take < 0 || take > remainAmount) throw new IllegalArgumentException("invalid allocation");
        this.remainAmount -= take;
        this.updatedAt = Instant.now();
    }

    public void restore(long amount) {
        this.remainAmount += amount;
        this.updatedAt = Instant.now();
    }

}
