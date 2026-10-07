package property.manager.dto.cost.property;

import jakarta.validation.constraints.Positive;

public record UpdatePropertyCostRequestDto(
        @Positive
        Long propertyId,
        @Positive
        Long utilityInvoiceId
) {
}
