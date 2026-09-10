package property.manager.service;

import property.manager.dto.unit.CreateUnitRequestDto;
import property.manager.dto.unit.UnitResponseDto;

public interface UnitService {
    UnitResponseDto createUnit(CreateUnitRequestDto request);

    UnitResponseDto getUnit(Long id);
}
