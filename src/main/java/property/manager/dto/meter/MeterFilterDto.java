package property.manager.dto.meter;

import jakarta.validation.constraints.Positive;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;

public record MeterFilterDto(
        @Positive
        Long propertyId,
        @Positive
        Long unitId,
        MeasurementType measurementType,
        MeterPurpose purpose,
        Boolean active
) {
}
