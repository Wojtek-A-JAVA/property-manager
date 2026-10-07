package property.manager.dto.settlement;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.validator.constraints.UniqueElements;
import property.manager.model.UtilityType;

public record CreateSettlementRequestDto(
        @NotNull
        @NotEmpty
        @UniqueElements
        List<UtilityType> utilityTypes
) {
}
