package property.manager.dto.meter;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MeterReadingResponseDto(
        Long id,
        Long meterId,
        YearMonth readingMonth,
        BigDecimal value,
        String notes
) {
}
