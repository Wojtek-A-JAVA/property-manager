package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.property.CreatePropertyRequestDto;
import property.manager.dto.property.PropertyResponseDto;
import property.manager.model.Property;

@Mapper(config = MapperConfig.class)
public interface PropertyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Property toEntity(CreatePropertyRequestDto request);

    @Mapping(target = "unitIds", ignore = true)
    PropertyResponseDto toDto(Property property);

    List<PropertyResponseDto> toDtoList(List<Property> properties);
}
