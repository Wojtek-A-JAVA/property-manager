package property.manager.service;

import java.util.List;
import property.manager.dto.cost.unit.CreateUnitCostRequestDto;
import property.manager.dto.cost.unit.CreateUnitCostsRequestDto;
import property.manager.dto.cost.unit.UnitCostFilterRequestDto;
import property.manager.dto.cost.unit.UnitCostResponseDto;
import property.manager.dto.cost.unit.UpdateUnitCostNotesRequestDto;
import property.manager.dto.cost.unit.UpdateUnitCostRequestDto;
import property.manager.model.PropertyCost;
import property.manager.model.UnitCost;
import property.manager.model.UtilityType;

public interface UnitCostService {

    UnitCostResponseDto createUnitCost(CreateUnitCostRequestDto request);

    List<UnitCostResponseDto> createUnitCosts(CreateUnitCostsRequestDto request);

    UnitCostResponseDto getUnitCost(Long id);

    List<UnitCostResponseDto> getUnitCosts(UnitCostFilterRequestDto filter);

    UnitCostResponseDto updateUnitCost(Long id, UpdateUnitCostRequestDto request);

    UnitCostResponseDto updateUnitCostNotes(Long id, UpdateUnitCostNotesRequestDto request);

    List<UnitCost> createUnitCostEntities(PropertyCost propertyCost, UtilityType utilityType);
}
