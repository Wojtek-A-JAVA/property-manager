package property.manager.dto.cost.property;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import property.manager.model.UtilityType;

public record PropertyCostFilterDto(
        @Positive
        Long propertyId,
        YearMonth billingMonthFrom,
        YearMonth billingMonthTo,
        BigDecimal amountFrom,
        BigDecimal amountTo,
        List<UtilityType> utilityTypes,
        Boolean hasNotes
) {
}
