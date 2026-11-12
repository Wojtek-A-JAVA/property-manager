package property.manager.dto.lease;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;

public record CreateLeaseRequestDto(
        @NotNull
        @Positive
        Long tenantId,
        @NotNull
        @NotEmpty
        List<Long> unitIds,
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
        @Min(1)
        Integer noticePeriodMonths,
        LeaseStatus leaseStatus,
        String notes
) {
}
