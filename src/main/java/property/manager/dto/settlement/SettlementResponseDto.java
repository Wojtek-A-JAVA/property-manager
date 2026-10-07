package property.manager.dto.settlement;

import java.util.List;

public record SettlementResponseDto(
        Long invoiceId,
        List<Long> propertyCostIds,
        List<Long> unitCostIds
) {
}
