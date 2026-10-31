package property.manager.dto.meter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateMeterReadingRequestDto(
        @NotNull
        @DecimalMin("0.0")
        BigDecimal value,
        String notes
) {
}
