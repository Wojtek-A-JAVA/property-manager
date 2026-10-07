package property.manager.dto.supplier;

import jakarta.validation.constraints.NotNull;

public record SupplierStatusRequestDto(
        @NotNull
        Boolean active
) {
}
