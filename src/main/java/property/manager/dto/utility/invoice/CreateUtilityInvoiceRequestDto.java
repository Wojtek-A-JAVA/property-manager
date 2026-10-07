package property.manager.dto.utility.invoice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import property.manager.model.UtilityType;

public record CreateUtilityInvoiceRequestDto(
        @NotNull
        @Positive
        Long propertyId,
        @NotNull
        UtilityType utilityType,
        @NotNull
        @Positive
        Long supplierId,
        @NotBlank
        String invoiceNumber,
        @NotNull
        LocalDate invoiceDate,
        @NotNull
        LocalDate billingFrom,
        @NotNull
        LocalDate billingTo,
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,
        @DecimalMin("0.001")
        BigDecimal consumption,
        String notes
) {
}
