package com.musinsa.freepoint.adapters.in.web.dto;


public record AccrualResponse(
        Long accrualId,
        String userId,
        long amount,
        long remainAmount,
        boolean manual
) {}