package property.manager.dto.unit;

import java.math.BigDecimal;
import property.manager.model.UnitType;

public record UnitResponseDto(
        Long id,
        Long propertyId,
        String name,
        String unitNumber,
        UnitType type,
        BigDecimal area,
        boolean active
) {
}
