package property.manager.dto.lease;

import jakarta.validation.constraints.NotNull;
import property.manager.model.LeaseStatus;

public record LeaseStatusRequestDto(
        @NotNull
        LeaseStatus status
) {
}
