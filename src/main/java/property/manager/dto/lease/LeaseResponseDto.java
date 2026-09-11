package property.manager.dto.lease;

import java.math.BigDecimal;
import java.time.LocalDate;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;

public record LeaseResponseDto(
        Long id,
        Long tenantId,
        Long unitId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal rentNetAmount,
        BigDecimal additionalCostsNetAmount,
        BigDecimal depositAmount,
        VatRate vatRate,
        Integer paymentDueDay,
        LeaseStatus leaseStatus,
        String notes
) {
}
