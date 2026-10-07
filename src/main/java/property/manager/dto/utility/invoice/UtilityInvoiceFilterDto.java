package property.manager.dto.utility.invoice;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import property.manager.model.UtilityType;

public record UtilityInvoiceFilterDto(
        @Positive
        Long propertyId,
        UtilityType utilityType,
        String supplierTaxId,
        LocalDate invoiceDateFrom,
        LocalDate invoiceDateTo,
        LocalDate billingFrom,
        LocalDate billingTo,
        BigDecimal amountFrom,
        BigDecimal amountTo
) {
}
