package property.manager.dto.settlement;

import java.util.List;

public record SettlementResponseDto(
        List<Long> propertyCostIds,
        List<Long> unitCostIds
) {
}
