package property.manager.dto.lease;

import java.time.LocalDate;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;

public record LeaseFilterDto(
        LeaseStatus status,
        Long tenantId,
        Long unitId,
        VatRate vatRate,
        LocalDate startDate,
        LocalDate endDate,
        boolean openEnded


) {
}
