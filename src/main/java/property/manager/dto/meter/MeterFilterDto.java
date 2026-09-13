package property.manager.dto.meter;

import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;

public record MeterFilterDto(
        Long propertyId,
        Long unitId,
        MeasurementType measurementType,
        MeterPurpose purpose,
        Boolean active
) {
}
