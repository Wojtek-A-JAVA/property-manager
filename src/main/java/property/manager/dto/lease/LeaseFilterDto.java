package property.manager.dto.lease;

import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import property.manager.model.LeaseStatus;
import property.manager.model.VatRate;

public record LeaseFilterDto(
        LeaseStatus status,
        @Positive
        Long tenantId,
        @Positive
        Long unitId,
        @Positive
        VatRate vatRate,
        LocalDate startDate,
        LocalDate endDate,
        Boolean openEnded
) {
}
