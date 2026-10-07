package property.manager.dto.cost.property;

import java.math.BigDecimal;
import java.time.YearMonth;
import property.manager.model.UtilityType;

public record PropertyCostResponseDto(
        Long id,
        Long propertyId,
        Long utilityInvoiceId,
        UtilityType utilityType,
        BigDecimal amount,
        YearMonth billingMonth,
        String notes
) {
}
