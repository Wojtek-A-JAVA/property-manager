package property.manager.dto.supplier;

import jakarta.validation.constraints.Pattern;

public record UpdateSupplierRequestDto(
        String name,
        @Pattern(regexp = "\\d{10}", message = "Tax ID must contain exactly 10 digits")
        String taxId
) {
}
