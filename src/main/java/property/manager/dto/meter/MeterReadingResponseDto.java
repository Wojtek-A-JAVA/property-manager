package property.manager.dto.meter;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MeterReadingResponseDto(
        Long id,
        Long meterId,
        LocalDate readingDate,
        BigDecimal value,
        String notes
) {
}
