package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.cost.property.PropertyCostResponseDto;
import property.manager.model.PropertyCost;

@Mapper(config = MapperConfig.class)
public interface PropertyCostMapper {

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "utilityInvoiceId", source = "utilityInvoice.id")
    @Mapping(target = "utilityType", source = "utilityInvoice.utilityType")
    PropertyCostResponseDto toDto(PropertyCost propertyCost);

    List<PropertyCostResponseDto> toDtoList(List<PropertyCost> propertyCosts);
}
