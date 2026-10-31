package property.manager.dto.settlement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import property.manager.model.RecalculationSource;
import property.manager.model.UtilityType;

public record RecalculateSettlementRequestDto(
        @NotNull
        RecalculationSource source,
        @NotNull
        @Positive
        Long id,
        List<UtilityType> utilityTypes
) {
}
