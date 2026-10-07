package property.manager.dto.tenant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import property.manager.model.TenantType;

public record CreateTenantRequestDto(
        @NotNull
        TenantType type,
        String firstName,
        String lastName,
        String companyName,
        @Pattern(regexp = "\\d{10}", message = "Tax ID must contain exactly 10 digits")
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
