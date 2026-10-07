package property.manager.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import property.manager.config.MapperConfig;
import property.manager.dto.supplier.CreateSupplierRequestDto;
import property.manager.dto.supplier.SupplierResponseDto;
import property.manager.model.Supplier;

@Mapper(config = MapperConfig.class)
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Supplier toEntity(CreateSupplierRequestDto request);

    SupplierResponseDto toDto(Supplier supplier);

    List<SupplierResponseDto> toDtoList(List<Supplier> suppliers);
}
