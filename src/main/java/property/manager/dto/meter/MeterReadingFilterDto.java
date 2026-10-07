package property.manager.dto.meter;

import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record MeterReadingFilterDto(
        @Positive
        Long meterId,
        LocalDate fromDate,
        LocalDate toDate
) {
}
