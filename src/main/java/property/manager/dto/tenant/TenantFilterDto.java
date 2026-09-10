package property.manager.dto.tenant;

import property.manager.model.TenantType;

public record TenantFilterDto(
        Boolean active,
        TenantType type
) {
}
