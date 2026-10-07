package property.manager.dto.utility.rule;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.YearMonth;
import java.util.List;
import property.manager.model.AllocationMethod;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;
import property.manager.model.UtilityType;

public record CreateUtilityRuleRequestDto(
        @NotNull
        @Positive
        Long propertyId,
        @NotNull
        UtilityType utilityType,
        @NotNull
        AllocationMethod allocationMethod,
        @NotNull
        Boolean unitMeterRequired,
        @NotNull
        Boolean propertyMeterRequired,
        MeasurementType measurementType,
        MeterPurpose meterPurpose,
        @NotEmpty
        List<@NotNull @Positive Long> unitIds,
        @NotNull
        YearMonth startMonth,
        YearMonth endMonth,
        String notes
) {
}
