
package com.musinsa.freepoint.application.service;

import com.musinsa.freepoint.adapters.out.persistence.JpaPointAccrualRepository;
import com.musinsa.freepoint.adapters.out.persistence.JpaPointWalletRepository;
import com.musinsa.freepoint.config.PolicyConfig;
import com.musinsa.freepoint.domain.accrual.PointAccrual;
import com.musinsa.freepoint.domain.wallet.PointWallet;
import com.musinsa.freepoint.application.port.in.Commands.AccrualCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AccrualUseCase {
    private final JpaPointAccrualRepository accrualRepo;
    private final JpaPointWalletRepository walletRepo;
    private final PolicyConfig policy;

    public AccrualUseCase(JpaPointAccrualRepository accrualRepo, JpaPointWalletRepository walletRepo, PolicyConfig policy) {
        this.accrualRepo = accrualRepo; this.walletRepo = walletRepo; this.policy = policy;
    }

    @Transactional
    public PointAccrual accrue(AccrualCommand cmd) {
        if (cmd.amount() < 1 || cmd.amount() > policy.getMaxAccrualPerTxn())
            throw new IllegalArgumentException("1회 적립 한도 위반");

        int expiry = cmd.expiryDays() != null ? cmd.expiryDays() : policy.getDefaultExpiryDays();
        if (expiry < policy.getMinExpiryDays() || expiry > policy.getMaxExpiryDays())
            throw new IllegalArgumentException("만료일 범위 위반");

        PointWallet wallet = walletRepo.findById(cmd.userId()).orElse(new PointWallet(cmd.userId()));
        if (wallet.getTotalBalance() + cmd.amount() > policy.getMaxWalletBalance())
            throw new IllegalArgumentException("보유 한도 초과");

        Instant expiresAt = Instant.now().plus(expiry, ChronoUnit.DAYS);
        PointAccrual a = PointAccrual.create(cmd.userId(), cmd.amount(), cmd.manual(), cmd.sourceType(), cmd.sourceId(), expiresAt);
        accrualRepo.save(a);
        wallet.increase(cmd.amount(), cmd.manual());
        walletRepo.save(wallet);
        return a;
    }
}
