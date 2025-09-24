
package com.musinsa.freepoint.domain.wallet;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "point_wallet")
public class PointWallet {
    @Id
    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "total_balance")
    private long totalBalance;

    @Column(name = "manual_balance")
    private long manualBalance;

    private Instant updatedAt = Instant.now();

    protected PointWallet() {}
    public PointWallet(String userId) { this.userId = userId; }

    public String getUserId() { return userId; }
    public long getTotalBalance() { return totalBalance; }
    public long getManualBalance() { return manualBalance; }

    public void increase(long amount, boolean manual) {
        this.totalBalance += amount;
        if (manual) this.manualBalance += amount;
        this.updatedAt = Instant.now();
    }
    public void decrease(long amount) {
        this.totalBalance -= amount;
        if (this.manualBalance > 0) {
            long dec = Math.min(this.manualBalance, amount);
            this.manualBalance -= dec;
        }
        this.updatedAt = Instant.now();
    }
}
