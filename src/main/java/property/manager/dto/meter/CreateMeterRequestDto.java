package property.manager.dto.meter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;

public record CreateMeterRequestDto(
        @NotNull
        @Positive
        Long propertyId,
        @Positive
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
