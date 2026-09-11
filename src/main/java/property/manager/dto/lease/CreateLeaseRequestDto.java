package property.manager.dto.lease;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;

public record CreateLeaseRequestDto(
        @NotNull
        Long tenantId,
        @NotNull
        Long unitId,
        @NotNull
        LocalDate startDate,
        LocalDate endDate,
        @NotNull
        @DecimalMin("0.00")
        BigDecimal rentNetAmount,
        @DecimalMin("0.00")
        BigDecimal additionalCostsNetAmount,
        @DecimalMin("0.00")
        BigDecimal depositAmount,
        @NotNull
        VatRate vatRate,
        @NotNull
        @Min(1)
        @Max(31)
        Integer paymentDueDay,
        LeaseStatus leaseStatus,
        String notes
) {
}
