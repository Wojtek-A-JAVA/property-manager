package property.manager.dto.meter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateMeterReadingRequestDto(
        @NotNull
        LocalDate readingDate,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal value,
        String notes
) {
}
