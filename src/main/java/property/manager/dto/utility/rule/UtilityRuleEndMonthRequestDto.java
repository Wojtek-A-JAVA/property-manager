package property.manager.dto.utility.rule;

import jakarta.validation.constraints.NotNull;
import java.time.YearMonth;

public record UtilityRuleEndMonthRequestDto(
        @NotNull
        YearMonth endMonth
) {
}
