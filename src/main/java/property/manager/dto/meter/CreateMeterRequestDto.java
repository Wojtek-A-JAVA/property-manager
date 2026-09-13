package property.manager.dto.meter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;

public record CreateMeterRequestDto(
        @NotNull
        Long propertyId,
        Long unitId,
        @NotNull
        MeasurementType measurementType,
        @NotNull
        MeterPurpose purpose,
        @NotBlank
        String name,
        String notes
) {
}
