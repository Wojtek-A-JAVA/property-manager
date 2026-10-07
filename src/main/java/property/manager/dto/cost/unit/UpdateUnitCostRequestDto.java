package property.manager.dto.cost.unit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import property.manager.model.UtilityType;

public record UpdateUnitCostRequestDto(
        @NotNull
        @Positive
        Long unitId,
        @NotNull
        @Positive
        Long propertyCostId,
        @NotNull
        UtilityType utilityType
) {
}
