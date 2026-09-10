package property.manager.dto.property;

import jakarta.validation.constraints.NotBlank;

public record CreatePropertyRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String address,
        String description
) {
}
