package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.meter.CreateMeterReadingRequestDto;
import property.manager.dto.meter.MeterReadingResponseDto;
import property.manager.model.MeterReading;

@Mapper(config = MapperConfig.class)
public interface MeterReadingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meter", ignore = true)
    MeterReading toEntity(CreateMeterReadingRequestDto requestDto);

    @Mapping(target = "meterId", source = "meter.id")
    MeterReadingResponseDto toDto(MeterReading meterReading);

    List<MeterReadingResponseDto> toDtoList(List<MeterReading> readings);
}
