package property.manager.dto.lease;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;

public record LeaseResponseDto(
        Long id,
        Long tenantId,
        List<Long> unitIds,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal rentNetAmount,
        BigDecimal additionalCostsNetAmount,
        BigDecimal depositAmount,
        VatRate vatRate,
        Integer paymentDueDay,
        Integer noticePeriodMonths,
        LeaseStatus leaseStatus,
        String notes
) {
}
