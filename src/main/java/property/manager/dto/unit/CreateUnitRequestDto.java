package property.manager.dto.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import property.manager.model.UnitType;

public record CreateUnitRequestDto(
        @NotNull
        Long propertyId,
        @NotBlank
        String name,
        @NotBlank
        String unitNumber,
        @NotNull
        UnitType type,
        BigDecimal area
) {
}
