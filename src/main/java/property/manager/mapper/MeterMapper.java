package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.meter.CreateMeterRequestDto;
import property.manager.dto.meter.MeterResponseDto;
import property.manager.model.Meter;

@Mapper(config = MapperConfig.class)
public interface MeterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "unit", ignore = true)
    Meter toEntity(CreateMeterRequestDto requestDto);

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "unitId", source = "unit.id")
    MeterResponseDto toDto(Meter meter);

    List<MeterResponseDto> toDtoList(List<Meter> meters);
}
