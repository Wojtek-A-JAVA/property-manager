package property.manager.dto.cost.unit;

import java.math.BigDecimal;
import java.time.YearMonth;

public record UnitCostResponseDto(
        Long id,
        Long unitId,
        Long propertyCostId,
        Long utilityRuleId,
        Long meterId,
        BigDecimal amount,
        BigDecimal consumption,
        YearMonth billingMonth,
        String notes
) {
}
