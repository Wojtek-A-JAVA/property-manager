package property.manager.dto.utility.invoice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import property.manager.model.UtilityType;

public record UpdateUtilityInvoiceRequestDto(
        @Positive
        Long propertyId,
        UtilityType utilityType,
        @Positive
        Long supplierId,
        String invoiceNumber,
        LocalDate invoiceDate,
        LocalDate billingFrom,
        LocalDate billingTo,
        @DecimalMin("0.01")
        BigDecimal amount,
        @DecimalMin("0.001")
        BigDecimal consumption,
        String notes
) {
}
