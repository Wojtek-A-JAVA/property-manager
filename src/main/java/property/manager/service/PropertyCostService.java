package property.manager.service;

import java.util.List;
import property.manager.dto.cost.property.CreatePropertyCostRequestDto;
import property.manager.dto.cost.property.PropertyCostFilterDto;
import property.manager.dto.cost.property.PropertyCostResponseDto;
import property.manager.dto.cost.property.UpdatePropertyCostRequestDto;
import property.manager.model.PropertyCost;
import property.manager.model.UtilityInvoice;

public interface PropertyCostService {
    List<PropertyCostResponseDto> createPropertyCost(CreatePropertyCostRequestDto request);

    PropertyCostResponseDto getPropertyCost(Long id);

    List<PropertyCostResponseDto> getPropertyCosts(PropertyCostFilterDto filter);

    List<PropertyCostResponseDto> updatePropertyCost(Long id, UpdatePropertyCostRequestDto request);

    List<PropertyCost> createPropertyCosts(UtilityInvoice utilityInvoice);
}
