
package com.musinsa.freepoint.domain.usage;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "point_usage_detail")
public class PointUsageDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usageId;
    private Long accrualId;
    private long amount;

    public static PointUsageDetail of(Long usageId, String accrualId, long amount) {
        PointUsageDetail d = new PointUsageDetail();
        //d.usageId = usageId;
        //d.accrualId = accrualId;
        // d.amount = amount;
        return d;
    }

}
