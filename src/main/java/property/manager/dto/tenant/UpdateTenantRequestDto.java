package property.manager.dto.tenant;

import jakarta.validation.constraints.Email;
import property.manager.validation.NullOrNotBlank;

public record UpdateTenantRequestDto(
        @NullOrNotBlank
        String firstName,
        @NullOrNotBlank
        String lastName,
        @NullOrNotBlank
        String companyName,
        @NullOrNotBlank
        String taxId,
        @NullOrNotBlank
        String pesel,
        @NullOrNotBlank
        String contactFirstName,
        @NullOrNotBlank
        String contactLastName,
        @NullOrNotBlank
        String address,
        @Email
        @NullOrNotBlank
        String email,
        @NullOrNotBlank
        String phone
) {
}
