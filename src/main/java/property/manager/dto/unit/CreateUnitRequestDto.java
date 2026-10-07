package property.manager.dto.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import property.manager.model.UnitType;

public record CreateUnitRequestDto(
        @NotNull
        @Positive
        Long propertyId,
        @NotBlank
        String name,
        @NotBlank
        String unitNumber,
        @NotNull
        UnitType type,
        @Positive
        BigDecimal area
) {
}
