package property.manager.dto.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateSupplierRequestDto(
        @NotBlank
        String name,
        @NotBlank
        @Pattern(regexp = "\\d{10}", message = "Tax ID must contain exactly 10 digits")
        String taxId
) {
}
