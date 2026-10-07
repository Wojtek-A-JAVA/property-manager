package property.manager.dto.utility.rule;

import java.time.YearMonth;
import java.util.List;
import property.manager.model.AllocationMethod;
import property.manager.model.MeasurementType;
import property.manager.model.MeterPurpose;
import property.manager.model.UtilityType;

public record UtilityRuleResponseDto(
        Long id,
        Long propertyId,
        UtilityType utilityType,
        AllocationMethod allocationMethod,
        Boolean unitMeterRequired,
        Boolean propertyMeterRequired,
        MeasurementType measurementType,
        MeterPurpose meterPurpose,
        List<Long> unitIds,
        YearMonth startMonth,
        YearMonth endMonth,
        String notes,
        Boolean active
) {
}
