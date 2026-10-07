package property.manager.dto.tenant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import property.manager.validation.NullOrNotBlank;

public record UpdateTenantRequestDto(
        @NullOrNotBlank
        String firstName,
        @NullOrNotBlank
        String lastName,
        @NullOrNotBlank
        String companyName,
        @NullOrNotBlank
        @Pattern(regexp = "\\d{10}", message = "Tax ID must contain exactly 10 digits")
        String taxId,
        @NullOrNotBlank
        @Pattern(regexp = "\\d{11}", message = "PESEL must contain exactly 11 digits")
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
