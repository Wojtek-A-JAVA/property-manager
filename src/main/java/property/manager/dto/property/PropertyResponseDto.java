package property.manager.dto.property;

public record PropertyResponseDto(
        Long id,
        String name,
        String address,
        String description,
        boolean active
) {
}
