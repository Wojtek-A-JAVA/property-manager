package property.manager.dto.meter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.YearMonth;

public record CreateMeterReadingRequestDto(
        @NotNull
        YearMonth readingMonth,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal value,
        String notes
) {
}
