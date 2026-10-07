package property.manager.dto.utility.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import property.manager.model.UtilityType;

public record UtilityInvoiceResponseDto(
        Long id,
        Long propertyId,
        UtilityType utilityType,
        Long supplierId,
        String invoiceNumber,
        LocalDate invoiceDate,
        LocalDate billingFrom,
        LocalDate billingTo,
        BigDecimal amount,
        BigDecimal consumption,
        BigDecimal unitPrice,
        String notes
) {
}
