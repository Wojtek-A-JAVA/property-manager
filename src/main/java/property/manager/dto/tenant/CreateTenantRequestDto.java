package property.manager.dto.tenant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import property.manager.model.TenantType;

public record CreateTenantRequestDto(
        @NotNull
        TenantType type,
        String firstName,
        String lastName,
        String companyName,
        String taxId,
        String pesel,
        String contactFirstName,
        String contactLastName,
        String address,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String phone
) {
}
