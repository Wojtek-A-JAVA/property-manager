package property.manager.dto.meter;

import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;

public record MeterResponseDto(
        Long id,
        Long propertyId,
        Long unitId,
        MeasurementType measurementType,
        MeterPurpose purpose,
        String name,
        String notes,
        boolean active
) {
}
