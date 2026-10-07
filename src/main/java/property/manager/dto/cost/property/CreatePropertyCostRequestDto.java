package property.manager.dto.cost.property;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePropertyCostRequestDto(
        @NotNull
        @Positive
        Long propertyId,
        @NotNull
        @Positive
        Long utilityInvoiceId
) {
}
