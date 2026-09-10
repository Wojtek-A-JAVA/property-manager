package property.manager.dto.error;

import java.time.OffsetDateTime;

public record ErrorResponseDto(
        OffsetDateTime timestamp,
        int status,
        String error,
        String path,
        String message
) {
}
