package property.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.unit.CreateUnitRequestDto;
import property.manager.dto.unit.UnitResponseDto;
import property.manager.model.Unit;

@Mapper(config = MapperConfig.class)
public interface UnitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "active", constant = "true")
    Unit toEntity(CreateUnitRequestDto request);

    @Mapping(target = "propertyId", source = "property.id")
    UnitResponseDto toDto(Unit unit);
}
