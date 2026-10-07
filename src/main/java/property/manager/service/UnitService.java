package property.manager.service;

import java.util.List;
import property.manager.dto.unit.CreateUnitRequestDto;
import property.manager.dto.unit.UnitResponseDto;

public interface UnitService {
    UnitResponseDto createUnit(CreateUnitRequestDto request);

    UnitResponseDto getUnit(Long id);

    UnitResponseDto toggleActiveStatus(Long id);

    List<UnitResponseDto> getUnits();
}
