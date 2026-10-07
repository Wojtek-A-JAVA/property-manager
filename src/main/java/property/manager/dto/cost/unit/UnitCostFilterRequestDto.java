package property.manager.dto.cost.unit;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import property.manager.model.AllocationMethod;
import property.manager.model.UtilityType;

public record UnitCostFilterRequestDto(
        @Positive
        Long propertyId,
        @Positive
        Long unitId,
        YearMonth billingMonthFrom,
        YearMonth billingMonthTo,
        BigDecimal amountFrom,
        BigDecimal amountTo,
        List<UtilityType> utilityTypes,
        AllocationMethod allocationMethod,
        @Positive
        Long meterId
) {
}
