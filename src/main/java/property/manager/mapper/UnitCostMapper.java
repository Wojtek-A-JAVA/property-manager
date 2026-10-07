package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.cost.unit.UnitCostResponseDto;
import property.manager.model.UnitCost;

@Mapper(config = MapperConfig.class)
public interface UnitCostMapper {

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "propertyCostId", source = "propertyCost.id")
    @Mapping(target = "utilityRuleId", source = "utilityRule.id")
    @Mapping(target = "meterId", source = "meter.id")
    UnitCostResponseDto toDto(UnitCost unitCost);

    List<UnitCostResponseDto> toDtoList(List<UnitCost> unitCostList);
}
