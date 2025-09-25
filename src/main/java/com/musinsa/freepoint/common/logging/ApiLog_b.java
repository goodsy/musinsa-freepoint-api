
package com.musinsa.freepoint.common.logging;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "api_log")
public class ApiLog_b {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String requestId;
    private String method;
    private String path;
    private String userId;
    private String orderNo;
    private String idempotencyKey;
    private Integer status;
    private Integer tookMs;
    @Lob
    private String reqBody;
    @Lob
    private String resBody;
    private Instant createdAt = Instant.now();

    public static ApiLog_b of(String requestId, String method, String path, String userId, Integer status, int tookMs, String reqBody, String resBody) {
        ApiLog_b l = new ApiLog_b();
        l.requestId = requestId;
        l.method = method;
        l.path = path;
        l.userId = userId;
        l.status = status;
        l.tookMs = tookMs;
        l.reqBody = reqBody;
        l.resBody = resBody;
        return l;
    }
}
