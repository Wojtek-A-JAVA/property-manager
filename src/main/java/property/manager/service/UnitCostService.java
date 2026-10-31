package property.manager.service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
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

    List<UnitCostResponseDto> getUnitCostsByUtilityRuleId(UnitCostFilterRequestDto filter);

    UnitCostResponseDto updateUnitCost(Long id, UpdateUnitCostRequestDto request);

    UnitCostResponseDto updateUnitCostNotes(Long id, UpdateUnitCostNotesRequestDto request);

    List<UnitCost> createUnitCostEntities(PropertyCost propertyCost, UtilityType utilityType);

    List<UnitCost> getUnitCostsEntitiesByUtilityRuleId(Long utilityRuleId);

    void deleteUnitCostsByPropertyCostId(Long propertyCostId);

    void deleteUnitCostsByUtilityRuleId(Long utilityRuleId);

    void deleteUnitCostById(Long unitCostId);

    void deleteUnitCostsByPropertyCostIdAndMeterId(Long propertyCostId, Long meterId);

    UnitCost createUnitCostEntity(Long unitId,Long propertyCostId, UtilityType utilityType);

    UnitCost getUnitCostEntityForUnitAndRule(Long unitId, Long propertyCostId, Long utilityRuleId);

    Optional<UnitCost> findUnitCostEntityForUnitMeterAndBillingMonth(
            Long unitId, Long meterId, YearMonth billingMonth);

    List<UnitCost> findUnitCostEntitiesForPropertyMeterAndBillingMonth(
            Long id, YearMonth billingMonth);
}
