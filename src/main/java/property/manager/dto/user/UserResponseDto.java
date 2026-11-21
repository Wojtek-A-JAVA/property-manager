package property.manager.dto.user;

import property.manager.model.RoleName;

public record UserResponseDto(
        String username,
        RoleName role
) {
}
