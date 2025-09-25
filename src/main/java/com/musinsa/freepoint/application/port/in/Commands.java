
package com.musinsa.freepoint.application.port.in;

import com.musinsa.freepoint.adapters.in.web.dto.AccrualRequest;

public class Commands {
    //public record AccrualCommand(String userId, long amount, Integer expiryDays, boolean manual, String sourceType, String sourceId) {}
    public record AccrualCommand(AccrualRequest request) {}
    public record UseCommand(String userId, String orderNo, long amount) {}
    public record CancelUseCommand(Long usageId, long amount, String reason) {}
}
