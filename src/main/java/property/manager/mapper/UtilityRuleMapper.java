package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import property.manager.config.MapperConfig;
import property.manager.dto.utility.rule.CreateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UpdateUtilityRuleRequestDto;
import property.manager.dto.utility.rule.UtilityRuleResponseDto;
import property.manager.model.Unit;
import property.manager.model.UtilityRule;

@Mapper(config = MapperConfig.class)
public interface UtilityRuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "units", ignore = true)
    @Mapping(target = "active", constant = "true")
    UtilityRule toEntity(CreateUtilityRuleRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "units", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(
            UpdateUtilityRuleRequestDto request, @MappingTarget UtilityRule rule);

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "unitIds", source = "units")
    UtilityRuleResponseDto toDto(UtilityRule utilityRule);

    List<UtilityRuleResponseDto> toDtoList(List<UtilityRule> utilityRules);

    default Long map(Unit unit) {
        return unit.getId();
    }
}
