package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.model.Unit;
import property.manager.model.UtilityRule;
import property.manager.model.UtilityRuleHistory;

@Mapper(config = MapperConfig.class)
public interface UtilityRuleHistoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilityRuleId", source = "id")
    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "unitIds", source = "units")
    @Mapping(target = "ruleCreatedAt", source = "createdAt")
    @Mapping(target = "archivedAt", ignore = true)
    UtilityRuleHistory toHistory(UtilityRule rule);

    default List<Long> map(List<Unit> units) {
        return units.stream().map(Unit::getId).toList();
    }
}
