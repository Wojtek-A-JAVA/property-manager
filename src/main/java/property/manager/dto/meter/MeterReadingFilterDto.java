package property.manager.dto.meter;

import jakarta.validation.constraints.Positive;
import java.time.YearMonth;

public record MeterReadingFilterDto(
        @Positive
        Long meterId,
        YearMonth fromMonth,
        YearMonth toMonth
) {
}
