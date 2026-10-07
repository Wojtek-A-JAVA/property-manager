package property.manager.dto.property;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PropertyResponseDto {
    private Long id;
    private String name;
    private String address;
    private String description;
    private List<Long> unitIds;
    private Boolean active;
}
