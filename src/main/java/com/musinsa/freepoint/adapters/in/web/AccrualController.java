
package com.musinsa.freepoint.adapters.in.web;

import com.musinsa.freepoint.adapters.in.web.dto.AccrualRequest;
import com.musinsa.freepoint.adapters.in.web.dto.AccrualResponse;
import com.musinsa.freepoint.application.service.AccrualUseCase;
import com.musinsa.freepoint.application.port.in.Commands.AccrualCommand;
import com.musinsa.freepoint.common.idempotency.Idempotent;
import com.musinsa.freepoint.domain.accrual.PointAccrual;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/points/accruals")
public class AccrualController {
    private final AccrualUseCase useCase;
    public AccrualController(AccrualUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    @Idempotent
    public AccrualResponse accrue(@RequestBody AccrualRequest request) {

        PointAccrual accrual = useCase.accrue(new AccrualCommand(request));

        return new AccrualResponse(
                accrual.getId(),
                accrual.getUserId(),
                accrual.getAmount(),
                accrual.getRemainAmount(),
                accrual.isManual()
        );
    }
}
