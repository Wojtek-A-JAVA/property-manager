package property.manager.dto.supplier;

public record SupplierResponseDto(
        Long id,
        String name,
        String taxId,
        Boolean active
) {
}
