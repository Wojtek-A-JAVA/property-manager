package property.manager.dto.meter;

import java.time.LocalDate;

public record MeterReadingFilterDto(
        Long meterId,
        LocalDate fromDate,
        LocalDate toDate
) {
}
