
package com.musinsa.freepoint.common.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Aspect
@Component
public class ApiLogAspect {
    private final ApiLogRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApiLogAspect(ApiLogRepository repository) {
        this.repository = repository;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attr != null ? attr.getRequest() : null;

        var response = attr != null ? attr.getResponse() : null;
        ContentCachingResponseWrapper wrap = response instanceof ContentCachingResponseWrapper ?
                (ContentCachingResponseWrapper) response : new ContentCachingResponseWrapper(response);

        Object result = pjp.proceed();

        int took = (int) (System.currentTimeMillis() - start);
        int status = wrap.getStatus();
        String reqBody = ""; // can be enhanced
        String resBody = new String(wrap.getContentAsByteArray());
        repository.save(ApiLog_b.of(null, req != null ? req.getMethod() : "NA",
                req != null ? req.getRequestURI() : "NA", null, status, took, reqBody, resBody));
        wrap.copyBodyToResponse();
        return result;
    }
}
