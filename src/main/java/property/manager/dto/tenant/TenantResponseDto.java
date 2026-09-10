package property.manager.dto.tenant;

import property.manager.model.TenantType;

public record TenantResponseDto(
        Long id,
        TenantType type,
        String firstName,
        String lastName,
        String companyName,
        String taxId,
        String pesel,
        String contactFirstName,
        String contactLastName,
        String address,
        String email,
        String phone,
        boolean active
) {
}
