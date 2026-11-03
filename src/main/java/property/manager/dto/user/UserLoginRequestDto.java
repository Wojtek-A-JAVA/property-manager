package property.manager.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank
        @Schema(description = "Username", example = "demo_admin")
        String username,
        @NotBlank
        @Schema(description = "Password", example = "demo_admin_password")
        String password
) {
}
